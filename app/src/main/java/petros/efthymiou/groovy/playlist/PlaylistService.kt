package petros.efthymiou.groovy.playlist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException

class PlaylistService(private val api: PlaylistAPI)
{
    suspend fun  fetchPlaylists(): Flow<Result<List<PlayList>>>
    {
        //TODO("Not yet implemented")


        //return api.fetchAllPlaylists()


    /*
        val flowToReturn: Flow<Nothing> = flow{

    }
    */



        val  flowToReturn :Flow<Result<List<PlayList>>>  = flow{
                                     //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                     //api METHOD RETURNS THE MOCKED List<Playlist>
                                     //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            this.emit(Result.success(api.fetchAllPlaylists()))
        }.catch {
            this.emit(Result.failure(RuntimeException("Something went wrong")))
        }


        return  flowToReturn





    }

}
