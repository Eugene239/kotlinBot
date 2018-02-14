import com.epavlov.dao.UserDAO
import com.epavlov.repository.TrackRepository
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) {
    runBlocking {
        val list= TrackRepository.getList().filter { track -> track.parserCode!=0 }
        println("result: ${list.size}")
        list.forEach{ track->
            track.users.forEach{
                UserDAO.deleteTrack(it.value,track.id!!)
                delay(1000)
            }
        }
    }
}