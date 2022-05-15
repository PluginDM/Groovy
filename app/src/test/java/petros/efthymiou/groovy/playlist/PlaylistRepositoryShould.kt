package petros.efthymiou.groovy.playlist


import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test


import petros.efthymiou.groovy.utils.BaseUnitTest
import java.lang.RuntimeException

class PlaylistRepositoryShould:BaseUnitTest()
{
    val service: PlaylistService = mock()

    val playlists: List<PlayList> = mock()

    val exception = RuntimeException("Something went wrong")

    //ALTERNATIVE DEFINITION
    //val playlists = mock<List<PlayList>>()


    @Test
    fun getPlaylistFromService()
    {
        runBlocking {

            //THE SYSTEM UNDER TEST - SO WE REQUIRE IT BE THE REAL OBJECT
            val repository = PlaylistRepository(service)

            repository.getPlaylists()

            verify(service, times(1)).fetchPlaylists()
        }
    }

    @Test
    fun emitPlaylistFromService() = runBlocking {

        val repository = mockSuccessfulCase()

        //kotlinx.coroutines.flow.flow
                                                     //Gets first emission of Flow
        assertEquals(playlists, repository.getPlaylists().first().getOrNull())


        /*  INITIALLY FAILS - for the right reason - WITH THE FOLLOWING:

           Expected at least one element
java.util.NoSuchElementException: Expected at least one element

            ...BECAUSE PlaylistService WAS NOT EMITTING ANY DATA

            ...I.E. IT WAS SIMPLY RETURNING AN EMPTY Flow - THUS:

      ///////////////////////////////

            val flowToReturn:Flow<Result<List<PlayList>> > = flow {

        }
        return  flowToReturn

      ///////////////////////////////
    */

      /* NEXT FAILS WITH THIS - BECAUSE WE WEREN'T UNWRAPPING THE RESULT FROM THE
         Result OBJECT   !!!!!!!!!!

       Expected :Mock for List, hashCode: 79165483
Actual   :Success(Mock for List, hashCode: 79165483)


       ....TO CORRECT THIS - WE NEED TO CALL THE Result.getOrNull() METHOD ON THE
           VALUE THAT'S RETURNED BY Flow.first()


           ...I.E. NEEDS TO BE:
            Flow.first().getOrNull()


         */

    } //END METHOD


    @Test
    fun propagateErrors() = runBlocking    {

        whenever (service.fetchPlaylists()).thenReturn (

                    flow {
                        emit(Result.failure<List<PlayList>>(exception))
                    }
                )

        val repository = PlaylistRepository(service)

        assertEquals(exception, repository.getPlaylists().first().exceptionOrNull())

    }

    private suspend fun mockSuccessfulCase(): PlaylistRepository {

        val repository = mockFailureCase()

        return repository
    }

    private suspend fun mockFailureCase(): PlaylistRepository {
        whenever(service.fetchPlaylists()).thenReturn(
            flow {

                this.emit(Result.success(playlists))
            }
        )

        val repository = PlaylistRepository(service)
        return repository
    }


}