package com.epavlov.parsers.pochtaru.entity

class TrackPochta(val list: Array<List>) {

    class List(val formF22Params: FormF22Params, val lastOperationViewed: Boolean, val deleted: Boolean, val autoAdded: Boolean,
               val payable: Boolean, val euv: Boolean, val trackingItem: TrackingItem) {

        class FormF22Params(val ordered: Boolean, val weightGr: Long, val sendingType: String, val smallPackage: Boolean, val postId: String, val recipientIndex: String)

        class TrackingItem(val destinationCountryName: String,
                           val destinationCountryNameGenitiveCase: String, val originCountryName: String, val originCityName: OriginCityName, val mailRank: Long,
                           val mailCtg: Long, val postMark: Long, val insurance: Insurance, val isDestinationInInternationalTracking: Boolean,
                           val isOriginInInternationalTracking: Boolean, val futurePathList: Array<FuturePathList>, val cashOnDeliveryEventsList: CashOnDeliveryEventsList,
                           val sender: String, val recipient: String, val weight: Long, val storageTime: Long, val title: String, val liferayWebContentId: LiferayWebContentId,
                           val trackingHistoryItemList: Array<TrackingHistoryItemList>, val lastOperationTimezoneOffset: Long, val globalStatus: String, val mailType: String,
                           val mailTypeCode: Long, val countryFromCode: Long, val countryToCode: Long, val customDuty: CustomDuty, val cashOnDelivery: CashOnDelivery,
                           val indexFrom: IndexFrom, val indexTo: String, val canBeOrdered: Boolean, val canBePickedUp: Boolean, val deliveryOrderDate: DeliveryOrderDate,
                           val commonStatus: String, val firstOperationDate: Long, val lastOperationDate: Long, val barcode: String, val endStorageDate: EndStorageDate,
                           val hasBeenGiven: Boolean?, val lastOperationAttr: Long, val lastOperationType: Long, val id: Id) {

            class OriginCityName

            class Insurance

            class FuturePathList(val humanStatus: String, val operationType: Long, val operationAttr: Long, val countryId: Long,
                                 val countryName: String, val countryNameGenitiveCase: String)

            class CashOnDeliveryEventsList

            class LiferayWebContentId

            class TrackingHistoryItemList(val date: String, val humanStatus: String, val operationType: Long,
                                          val operationAttr: Long, val countryId: Long, val index: String, val cityName: String,
                                          val countryName: String, val countryNameGenitiveCase: String, val isInInternationalTracking: Boolean,
                                          val description: String, val weight: Long)

            class CustomDuty
            class CashOnDelivery
            class IndexFrom
            class DeliveryOrderDate
            class EndStorageDate
            class Id
        }
    }
}