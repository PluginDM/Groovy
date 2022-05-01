package petros.efthymiou.groovy.playlist

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {

    //!!!!NOTE: lateinit var CANNOT BE A PRIMITIVE OR A PROPERTY  !!!!
    /*public/private*/ lateinit var someNonPrimitive: String

    //val playlists = MutableLiveData<List<PlayList>>()

    //NOTE: Wrapping Kotlin Result around values effects clean error handling !!!!
    //val playlists = MutableLiveData<Result<List<PlayList>> >()


    //init {

    /* [CoroutineScope] tied to this [ViewModel].
        * This scope will be canceled when ViewModel will be cleared,
        * i.e [ViewModel.onCleared] is called
        *
        * This scope is bound to
        * [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
        */
    ///////////////////////////////////////////////////
    /* A VIEW MODEL THAT KNOWS WHEN IT IS BEING KILLED -
           AND THAT THEREFORE KILLS ALL COROUTINES THAT HAVE
           BEEN LAUNCHED IN THE SCOPE.
         */

    //NOTE: viewModelScope IS INHERITED FROM ViewModel
    //viewModelScope.launch {


    ///////////////////////////////////////////////////
    /*NOTE: repositoryCAN ONLY BE ACCESSED IN init BLOCK -
                BECAUSE WE ARE EFFECTIVELY DOING A lateinit OF THE
                CONSTRUCTOR PARAMETER !!!!!!
         */

    /*getPlaylists() IS A suspend FUNCTION - SO FOLLOWING DOES
          NOT COMPILE IF CALLED OUTSIDE OF A COROUTINE
         */
    /*NOTE: !!!!! RECALL THAT THIS METHOD IS MOCKED IN PlayListViewModelShould::emitPlaylistFromRepository()

               ... BY THE CALL AT THE FOLLOWING STATEMENT: var viewModel = PlaylistViewModel(repository)

               ...AFTER WHICH WE ARE ABLE TO COLLECT THE EMITTED Result<List<Playlist>>

               NOTE: collect PEELS OFF THE FlowCollector ... to leave a !!!naked!!!!
                                   Result<List<Playlist>>

         */

    /*
        repository.getPlaylists().collect {
            playlists.value = it as (Result<List<PlayList>>)   //NOTE: Cast is superfluous  !!!!!
            }

         */
    //NOTE: ABOVE PROPERTY ASSIGNMENT ULTIMATELY INVOKES MutableLiveData.setValue()


    //}
    ///////////////////////////////////////////////////
    //} END init


    /*
    // a simple LiveData that receives value 3, 3 seconds after being observed for the first time.
    val data : LiveData<Int> = liveData<Int> {
        delay(3000)
        emit(3)
    }

     */

    //  REFACTOR - NEATER WAY THA collect SCHEME USES LiveData builder/emitter !!!!!!
    //   NOTE: LiveDataScope REPLACES ViewModelScope   !!!!!!!
    //  REFACTOR - WE NOW INSTANTIATE VIA INSTANCE VARIABLE; RATHER THAN IN init BLOCK !!!!
    val playlists: LiveData<Result<List<PlayList>>> =


        liveData<Result<List<PlayList>>> {

            val myFlow: Flow<Result<List<PlayList>>> = repository.getPlaylists()




            //CALLED ON LiveDataScope i.e. this.emitSource()
            this.emitSource(myFlow.asLiveData())

            /*****
            myFlow.collect { it ->
                //NOTE: WHERE this IS A LiveDataScope
                //NOTE: WHERE it IA A Result<List<PlayList>>
                this.emit(it)
            }
            *****/




        }
}



/*
interface LiveDataScope<T> {

 Set's the [LiveData]'s value to the given [value]. If you've called [emitSource] previously,
 calling [emit] will remove that source.

Note that this function suspends until the value is set on the [LiveData].

  @param value The new value for the [LiveData]

 @see emitSource



suspend fun emit(value: T)


Add the given [LiveData] as a source, similar to [MediatorLiveData.addSource]. Calling this
 method will remove any source that was yielded before via [emitSource].

  @param source The [LiveData] instance whose values will be dispatched from the current
[LiveData].

  @see emit
  @see MediatorLiveData.addSource
 @see MediatorLiveData.removeSource

suspend fun emitSource(source: LiveData<T>): DisposableHandle


 References the current value of the [LiveData].

If the block never `emit`ed a value, [latestValue] will be `null`. You can use this
value to check what was then latest value `emit`ed by your `block` before it got cancelled.

  Note that if the block called [emitSource], then `latestValue` will be last value
  dispatched by the `source` [LiveData].

val latestValue: T?
*/




