package petros.efthymiou.groovy.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import petros.efthymiou.groovy.R

/**
 * A fragment representing a list of Items.
 */
class PlayListFragment : Fragment() {

    //DECLARE DEPENDENCY
    lateinit var viewModel: PlaylistViewModel

    lateinit var viewModelFactory: PlaylistViewModelFactory

    private val repository = PlaylistRepository()


    /***********
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AUTO GENERATED - FOR FRAGMENT INPUT - NOT REQUIRED

        //arguments?.let {
        //    columnCount = it.getInt(ARG_COLUMN_COUNT)
        //}
        //

    }*********/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(
            R.layout.fragment_playlist,
            container, false)


        viewModelFactory = PlaylistViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(PlaylistViewModel::class.java)
        /*
         ViewModelProvider.get()

        Returns an existing ViewModel or creates a new one in the scope
        (usually, a fragment or an activity), associated with this ViewModelProvider.
        The created ViewModel is associated with the given scope and will be retained
        as long as the scope is alive (e.g. if it is an activity, until it is finished
        or process is killed).

        Params:
                modelClass – The class of the ViewModel to create an instance of it if it is
                not present.

        Type parameters:
                <T> – The type parameter for the ViewModel.

        Returns:
                A ViewModel that is an instance of the given type T.
         */
                                                            /*NOTE: Explicit construction
                                                                    of Observer can be
                                                                    omitted here.
                                                             */
        viewModel.playlists.observe(this as LifecycleOwner,
            /*Observer*/{ playlists ->
            with(view as RecyclerView /*Because we know the view is a RecyclerView */ ) {

                /*NOTE: Wrapping Kotlin Result around values - playlist here
                 effects clean error handling !!!! */

                if(playlists.getOrNull() != null)
                {
                    /*Properties of view - as obtained in with()
                    i.e the RecyclerView
                     */
                    ///////////////////////////////////////////////////
                    layoutManager = LinearLayoutManager(context)

                    adapter = MyPlayListRecyclerViewAdapter(playlists)
                    ///////////////////////////////////////////////////
                }
                else
                {
                    //TODO
                }
            }

        })

        /*************  NOT REQUIRED
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyPlayListRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
        **************/
        return view
    }

    companion object {
        /******* AUTO GENERATED - FOR FRAGMENT INPUT - NOT REQUIRED *****

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
         *****************************************************************/

         /******* AUTO GENERATED columnCount- FOR FRAGMENT INPUT - NOT REQUIRED *****/
        @JvmStatic
        fun newInstance(/*columnCount: Int*/) =
            PlayListFragment().apply {
            /******* AUTO GENERATED - FOR FRAGMENT INPUT - NOT REQUIRED *****
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
             *****************************************************************/
            }
    }
}