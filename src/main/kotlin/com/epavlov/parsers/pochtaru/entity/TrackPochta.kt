package com.epavlov.parsers.pochtaru.entity


data class TrackPochta(
		val list: List<Item0>
)

data class Item0(
		val officeSummary: Any, //null
		val postmanDeliveryInfo: Any, //null
		val formF22Params: FormF22Params,
		val userTrackingItemId: Any, //null
		val userTitle: Any, //null
		val itemAddedDate: Any, //null
		val deleteDate: Any, //null
		val lastOperationViewed: Boolean, //false
		val deleted: Boolean, //false
		val autoAdded: Boolean, //false
		val lastOperationViewedTimestamp: Any, //null
		val payable: Boolean, //false
		val paymentStatus: Any, //null
		val paymentDate: Any, //null
		val euv: Boolean, //false
		val amount: Any, //null
		val trackingItem: TrackingItem
)

data class FormF22Params(
		val WeightGr: Int, //406
		val SendingType: String, //Parcel
		val Parcel: Boolean, //true
		val PostId: String, //12161520001375
		val RecipientIndex: String //191124
)

data class TrackingItem(
		val destinationCountryName: String, //Россия
		val destinationCountryNameGenitiveCase: String, //России
		val originCountryName: String, //Россия
		val originCityName: String, //Москва
		val mailRank: Int, //0
		val mailCtg: Int, //3
		val postMark: Int, //2048
		val insurance: Any, //null
		val isDestinationInInternationalTracking: Boolean, //true
		val isOriginInInternationalTracking: Boolean, //true
		val futurePathList: List<FuturePath>,
		val cashOnDeliveryEventsList: Any, //null
		val sender: String, //Иванов Д. В.
		val recipient: String, //ЦУПРОВ ВЛАДИМИР
		val weight: Int, //406
		val storageTime: Int, //0
		val title: String, //Посылка из Москвы
		val liferayWebContentId: Any, //null
		val trackingHistoryItemList: List<TrackingHistoryItem>,
		val lastOperationTimezoneOffset: Int, //10800000
		val globalStatus: String, //IN_PROGRESS
		val mailType: String, //Посылка
		val mailTypeCode: Int, //4
		val countryFromCode: Int, //643
		val countryToCode: Int, //643
		val customDuty: Any, //null
		val cashOnDelivery: Any, //null
		val indexFrom: String, //121615
		val indexTo: String, //191124
		val canBeOrdered: Boolean, //false
		val canBePickedUp: Boolean, //false
		val deliveryOrderDate: Any, //null
		val commonStatus: String,
		val firstOperationDate: Long, //1518427834000
		val lastOperationDate: Long, //1518900814001
		val barcode: String, //12161520001375
		val endStorageDate: Any, //null
		val hasBeenGiven: Any, //null
		val lastOperationAttr: Int, //4
		val lastOperationType: Int, //8
		val id: Any //null
)

data class FuturePath(
		val date: Any, //null
		val humanStatus: String, //Доставка по России
		val operationType: Int, //8
		val operationAttr: Int, //-106
		val countryId: Int, //643
		val index: Any, //null
		val cityName: Any, //null
		val countryName: String, //Россия
		val countryNameGenitiveCase: String, //России
		val isInInternationalTracking: Any, //null
		val description: Any, //null
		val weight: Any //null
)

data class TrackingHistoryItem(
		val date: String, //2018-02-17T23:53:34.001+03:00
		val humanStatus: String, //Покинуло сортировочный центр
		val operationType: Int, //8
		val operationAttr: Int, //4
		val countryId: Int, //643
		val index: String, //140960
		val cityName: String, //Подольск
		val countryName: String, //Россия
		val countryNameGenitiveCase: String, //России
		val isInInternationalTracking: Boolean, //true
		val description: String, //Московский АСЦ
		val weight: Int //406
)