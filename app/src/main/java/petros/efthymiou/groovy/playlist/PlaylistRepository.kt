package petros.efthymiou.groovy.playlist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepository(private val service: PlaylistService )
{
    suspend fun getPlaylists(): Flow<Result<List<PlayList>> >
    {
        /*NOTE:  !!!! COMPILER DOES NOT COMPLAIN - WITH A
                  TODO - IF NOTHING IS RETURNED FOR A METHOD
                  THAT RETURNS A VALUE!!!!!!
         */
        //TODO("Not yet implemented")

        val flowToReturn = service.fetchPlaylists()

        //NOTE: WWE WERE RETURNING THIS PRIOR TO IMPLEMENTING service.fetchPlaylists()
        /*

         val flowToReturn:Flow<Result<List<PlayList>> > = flow {

        }

         */
        return  flowToReturn
    }


}
