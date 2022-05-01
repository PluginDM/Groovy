package petros.efthymiou.groovy.playlist

import kotlinx.coroutines.flow.Flow

class PlaylistRepository
{
    suspend fun getPlaylists(): Flow<Result<List<PlayList>> >
    {
        /*NOTE:  !!!! COMPILER DOES NOT COMPLAIN - WITH A
                  TODO - IF NOTHING IS RETURNED FOR A METHOD
                  THAT RETURNS A VALUE!!!!!!
         */
        TODO("Not yet implemented")
    }


}
