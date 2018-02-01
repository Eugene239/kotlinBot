package com.epavlov.parsers.track17.entity


data class Track17FirstResponse(
		val ret: Int, //1
		val msg: String, //Ok
		val g: String, //e0e635a421464863aa7ad6d69fc34304
		val dat: List<Dat>
)