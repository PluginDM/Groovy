package petros.efthymiou.groovy.playlist

import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import petros.efthymiou.groovy.utils.BaseUnitTest
import petros.efthymiou.groovy.utils.getValueForTest
import java.lang.RuntimeException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PlayListViewModelShould: BaseUnitTest() {
   /*** REPLACED BY BaseUnitTest - SO THAT REUSE IN FUTURE TESTS
    @get:Rule
    var coroutinesTestRule = MainCoroutineScopeRule()

    @get:Rule  //Allows immediate access to LiveData
    var instantTaskExecutorRule = InstantTaskExecutorRule()

   ***/

    //var viewModel: PlaylistViewModel

    /*NOT SYSTEM UNDER TEST - so we mock the object so that we can make a mock call to
                              method PlaylistRepository.getPlaylists()
     */
    var repository : PlaylistRepository = mock()

    var returnedException = RuntimeException("Something went wrong")


    /////////////////// FOR emitPlaylistFromRepository() ///////////
    private val playlists = mock<List<PlayList>>()
    /*
    success
        Returns an instance that encapsulates the given value as
        successful value.

        fun <T> success(value: T): Result<T>
     */
    /*Encapsulate data in Result object - using the *success builder* -  for
      ease of error handling
     */
    private val expected = Result.success(playlists)
    /////////////////////////////////////////////////////////////////

    init {
        /*
           CONCENTRATING ON ViewModel LAYER - SO MOCK CALL TO
           repository.getPlaylists() METHOD  !!!!!!!

           NOTE:  SIGNATURE OF repository.getPlaylists() IS THE FOLLOWING:
           Flow<Result<List<PlayList>>
         */


        /* GET FOLLOWING ERROR:
        Exception in thread "Test worker" java.lang.IllegalStateException: Module with the Main dispatcher had failed to initialize. For tests Dispatchers.setMain from kotlinx-coroutines-test module can be used
	at kotlinx.coroutines.internal.MissingMainCoroutineDispatcher.missing(MainDispatchers.kt:113)

	    ....
        IF FOLLOWING CODE IS NOT MOVED INTO TEST - OTHERWISE int BLOCK RUNS BEFORE
        MainCoroutineScopeRule() IS APPLIED !!!!!

        //NOTE:  COULD USE  runBlockingTest HERE - DOESN'T REALLY MATTER!!!
        runBlocking {
        whenever(repository.getPlaylists()).thenReturn(
            flow {
                emit(expected)
            }

        )
        }
         */



        /*THE SYSTEM UNDER TEST - the only one that has to be a real i.e
          not mock object - but which takes a mock repository.
         */
        //viewModel = PlaylistViewModel(repository)
    }

    @Test //I.E. ACTUALLY CALLS getPlaylists()
    fun getPlaylistsFromRepository() = runBlockingTest{


        var viewModel = mockSuccessfulCase()


        //METHOD FROM GOOGLE'S LiveDataValueCapture.
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Get the current value from a LiveData without
         needing to register an observer.
         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */

        /*NOTE: SUPERFLUOUS CALL - BECAUSE IT'S ONLY REQUIRED
                IN THE assertEquals() CHECK
         */

                  //WHERE playlists IS OF TYPE MutableLiveData<Result<List<Playlist>>
        viewModel.playlists.getValueForTest()




        /**
        FAILS FIRST WITH !!!!! - BECAUSE NO INSTANCE OF REPOSITORY
        Wanted but not invoked:
        playlistRepository.getPlaylists();
        -> at petros.efthymiou.groovy.playlist.PlaylistRepository.getPlaylists(PlaylistRepository.kt:5)
        Actually, there were zero interactions with this mock.

         **/
        //SEE IF METHOD repository.getPlaylists WAS CALLED 1 TIME
        verify(repository, times(1)).getPlaylists()
    }



    @Test
    fun emitPlaylistFromRepository() = runBlockingTest     {

        System.out.println("!!!!!£££££ " + playlists)// non null
        System.out.println(playlists == null) //false


        val viewModel = mockSuccessfulCase()

        //true
        System.out.println(viewModel.playlists.getValueForTest() == null)
        /**
        fun <T> LiveData<T>.getValueForTest(): T? {
        var value: T? = null
        var observer = Observer<T> {
        value = it

        System.out.println("+++++++> it=" + it)

        }

        System.out.println("+++++++> value=" + value)
        observeForever(observer)
        removeObserver(observer)
        return value
        }

         **/
        /*NOTE: RETURN VALUE OF getValueForTest() IS INITIALLY SET TO null. SEE ABOVE.
                SINCE THERE ARE IS NO DATA CHANGE, THERE ARE NO EMISSIONS - BECAUSE THE
                Observer DOESN'T SEE ANY CHANGES!!!!!

                THIS TEST THEREFORE INITIALLY FAILS!!!!!
         */

        System.out.println("viewModel.playlists.getValueForTest() = " + viewModel.playlists.getValueForTest())
        assertEquals(expected, viewModel.playlists.getValueForTest())

        /************************************************************
        NOTE: CALL TO viewModel.playlists.getValueForTest() ALLOWS US TO GET THE DATA WITHOUT
              THE NEED TO EXPLICITLY observe THE LIVEDATA

         viewModel.playlists.observe(this as LifecycleOwner,
            Observer{ playlists ->

            }

         ***********************************************************/



        /*
        expected:<Success(Mock for List, hashCode: 196037184)> but was:<Mock for List, hashCode: 196037184>
Expected :Success(Mock for List, hashCode: 196037184)
Actual   :Mock for List, hashCode: 196037184

         */
    }


    @Test
    fun emitErrorWhenReceiveError()
    {
        runBlocking{
            whenever(repository.getPlaylists()).
                   thenReturn(

                       flow{

                                                   //TYPE REQUIRED !!!
                           this.emit(Result.failure<List<PlayList>>(returnedException ))
                       }
                   )
        }

        val viewModel = PlaylistViewModel(repository)

        assertEquals(returnedException /* RuntimeException("Another exception")*/,
                                    //NULL ASSERTION - KNOW WHAT WE ARE DOING !!!
            viewModel.playlists.getValueForTest()!!.exceptionOrNull())


        /*
         !!!!!! NOTE: FAILS IF WE PASS THE FOLLOWING:  RuntimeException("Another exception") !!!!!!!

        expected:<java.lang.RuntimeException: Another exception> but was:<java.lang.RuntimeException: Something went wrong>
Expected :java.lang.RuntimeException: Another exception
Actual   :java.lang.RuntimeException: Something went wrong
         */
    }

    private fun mockSuccessfulCase(): PlaylistViewModel {
        //NOTE:  COULD USE  runBlockingTest HERE - DOESN'T REALLY MATTER!!!



        //public fun <T> flow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<T> = SafeFlow(block)

        //!!!! STIPULATE WHAT A CALL TO repository.getPlaylists() SHOULD FAKE
        runBlocking {
            val myFlow: Flow<Result<List<PlayList>>> = flow {

                this.emit(expected)
            }





            whenever(repository.getPlaylists()).thenReturn(
                /*
                 flow {

                    this.emit(expected)
                }
                */
                myFlow

                //        myFlow.collect {  }

            )


        }


        /*THE SYSTEM UNDER TEST - the only one that has to be a real i.e
          not mock object - but which takes a mock repository.

          NOTE: THERE HAS TO  BE AT LEAST 1 INTERACTION WITH THE DECLARED
                MOCK repository !!!!!

                ... OTHERWISE WE GET THE FOLLOWING MESSAGE:

                Actually, there were zero interactions with this mock;
                because the method repository.getPlaylists() WASN'T called.

                RECALL THE THE METHOD IS CALLED IN THE init BLOCK OF THE CLASS
                CONSTRUCTOR FOR PlaylistViewModel.

                ... SO THE FOLLOWING CALL IS REQUIRED
         */

        var viewModel = PlaylistViewModel(repository)

        return viewModel
    }


}