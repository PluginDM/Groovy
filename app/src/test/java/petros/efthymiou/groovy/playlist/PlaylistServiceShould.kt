package petros.efthymiou.groovy.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import petros.efthymiou.groovy.utils.BaseUnitTest
import java.lang.RuntimeException
import kotlin.coroutines.EmptyCoroutineContext

class PlaylistServiceShould:BaseUnitTest()
{
    //SUT
    private lateinit var service:PlaylistService

    //NOTE: MOCK CREATES AN ANONYMOUS OBJECT FROM INTERFACE
    private val api: PlaylistAPI = mock()

    private val playlists: List<PlayList>  = mock()

    @Test
    fun fetchPlaylistsFromAPI() = runBlockingTest   {

        service = PlaylistService(api)

        /*
           NOTE: THE CALL TO first() IS REQUIRED - IN ORDER TO !!!FORCE!!! AN OMISSION; OTHERWISE THE
           THIS TEST FAILS WHEN ALL TESTS IN PlaylistServiceShould ARE RUN; WITH THE FOLLOWING
           BEING REPORTED:

           Wanted but not invoked:
       playlistAPI.fetchAllPlaylists();
-> at petros.efthymiou.groovy.playlist.PlaylistServiceShould$fetchPlaylistsFromAPI$1.invokeSuspend(PlaylistServiceShould.kt:52)
Actually, there were zero interactions with this mock. I.E. THE api: PlaylistAPI = mock()
         */
        /////////////////////////////////
        service.fetchPlaylists().first()
        /////////////////////////////////


        /*
          ABOVE INITIALLY FAILS WITH FOLLOWING ERROR: because SUT i.e. PlaylistService -
          had not been implemented !!!!

          An operation is not implemented: Not yet implemented
kotlin.NotImplementedError: An operation is not implemented: Not yet implemented
	at petros.efthymiou.groovy.playlist.PlaylistService.fetchPlaylists(PlaylistService.kt:9)

         */


        //MOCK BEHAVIOUR OF OUR API
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //SEE IF THE api.fetchAllPlaylists() METHOD IS CALLED - ON THE
        //DEPENDENT OBJECT - api I.E. PlaylistAPI  - OF PlaylistService
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        verify(api, times(1)).fetchAllPlaylists()
    }

    @Test
    fun convertsValuesToFlowResultAndEmitsThem() =
        runBlockingTest {

            /*MOCK api.fetchAllPlaylists() -A DEPENDENCY OF
               PlaylistService    !!!!!!
             */
            mockSuccessfulCase()

            val firstElementOfFlow = service.fetchPlaylists().first()

            println("THE-FLOW = " + service.fetchPlaylists().javaClass )

            /* ABOVE STATEMENT REPORTS THE FOLLOWING:

            THE-FLOW = class kotlinx.coroutines.flow.SafeFlow
            THE-CLASS=class kotlin.Result
             */

            println("THE-CLASS=" +  firstElementOfFlow.javaClass )

            /* ABOVE STATEMENT REPORTS THE FOLLOWING:

                THE-CLASS=class kotlin.Result
             */

                                       //GRAB THE 1ST ELEMENT OF THE Flow
            //assertEquals(Result.success(playlists), firstElementOfFlow)
            assertEquals(Result.success(playlists),  service.fetchPlaylists().first())


            /****
            ABOVE RETURNS THE FOLLOWING WHEN service.fetchPlaylists() RETURNS
            Flow<Nothing>

            Expected at least one element
            java.util.NoSuchElementException: Expected at least one element
            ***/

       }//END runBlockingTest

    private suspend fun mockSuccessfulCase()
    {
        whenever(api.fetchAllPlaylists()).thenReturn(playlists)

        //THE System Under Test . INITIALISE WITH api as a dependency!!!!!
        service = PlaylistService(api)
    }

    @Test
    fun emitsErrorResultWhenNetworkFails() = runBlockingTest {

        mockErrorCase()

        assertEquals("Something went wrong",service.fetchPlaylists().first().exceptionOrNull()?.message)


    }

    private suspend fun mockErrorCase()
    {
        whenever(api.fetchAllPlaylists()).thenThrow(RuntimeException("Damn backend developers"))

        /* NOTE: THE ABOVE !!!RUNTIME!!! EXCEPTION IS THROWN BY THE CALL TO api.fetchAllPlaylists() - AND WE GET A
                 CRASH; BECAUSE THE EXCEPTION IS NOT CAUGHT OR TRANSFORMED.

                 WE THEREFORE HAVE TO CATCH THE EXCEPTION !!!

         */

        service = PlaylistService(api)
    }


}//END CLASS