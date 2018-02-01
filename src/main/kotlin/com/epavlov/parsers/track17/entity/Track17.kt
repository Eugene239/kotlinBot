package com.epavlov.parsers.track17.entity

data class Track17(
		val ret: Int, //1
		val msg: String, //Ok
		val g: String, //1c95c5225168494ca942a5a6478370dc
		val dat: List<Dat>
)

data class Dat(
		val no: String, //RF519862712SG
		val delay: Int, //0
		val yt: Any, //null
		val track: Track
)

data class Track(
		val b: Int, //1913
		val c: Int, //1803
		val e: Int, //10
		val f: Int, //-1
		val w1: Int, //19131
		val w2: Int, //18031
		val ln1: String, //en
		val ln2: String, //en
		val is1: Int, //1
		val is2: Int, //1
		val ygt1: Int, //389
		val ygt2: Int, //687
		val ylt1: String, //2018-02-01 19:36:20
		val ylt2: String, //2018-02-01 19:36:21
		val hs: Int, //-1865083392
		val yt: Any, //null
		val z0: Z0,
		val z1: List<Z1>,
		val z2: List<Z2>,
		val zex: Zex
)

data class Z1(
		val a: String, //2018-02-01 16:35
		val b: Any, //null
		val c: String,
		val d: String,
		val z: String //Arrival at Destination Post (Country: RU)
)

data class Z0(
		val a: String, //2018-02-01 17:13
		val b: Any, //null
		val c: String, //MR LC Vnukovo Cex-1 102976
		val d: String,
		val z: String //Customs clearance, Released by custom house Arzgir 356570
)

data class Z2(
		val a: String, //2018-02-01 17:13
		val b: Any, //null
		val c: String, //MR LC Vnukovo Cex-1 102976
		val d: String,
		val z: String //Customs clearance, Released by custom house Arzgir 356570
)

data class Zex(
		val sa: Boolean //false
)