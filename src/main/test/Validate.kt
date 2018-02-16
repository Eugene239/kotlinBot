import com.epavlov.repository.TrackRepository
import com.epavlov.repository.UserRepository
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) {
    //getBadTracskList()
    getBadUsers()
}

fun getBadTracskList() {
    runBlocking {
        val list = TrackRepository.getList().filter { track -> track.notFound > 0 }
        println("result: ${list.size}")
        list.forEach { track ->
            println(track.notFound)
        }
    }
}
fun getBadUsers(){
    runBlocking {
        val list= UserRepository.getList().filter { it.trackList.size==0  }
        println("result: ${list.size}")
        list.forEach { user ->
            println(user)
        }
    }
}
