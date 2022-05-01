package petros.efthymiou.groovy.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaylistViewModelFactory(private val repository
     : PlaylistRepository
):ViewModelProvider.Factory //An interface
{

    override fun <T : ViewModel?> create(modelClass: Class<T>
         /*AS SPECIFIED BY ViewModelProvider().get()  -
         IN THIS CASE PlaylistViewModel.class.java */): T
    {
        //TODO("Not yet implemented")

        return PlaylistViewModel(repository) as T

    }

}
