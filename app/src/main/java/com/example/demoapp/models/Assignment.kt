package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Assignment(
    @SerializedName("address") var address: String? = null,
    @SerializedName("assignmentId") var assignmentId: String? = null,
    @SerializedName("claimInsuredAddress1") var claimInsuredAddress1: String? = null,
    @SerializedName("claimInsuredAddress2") var claimInsuredAddress2: String? = null,
    @SerializedName("claimInsuredCity") var claimInsuredCity: String? = null,
    @SerializedName("claimInsuredMobilePhone") var claimInsuredMobilePhone: String? = null,
    @SerializedName("claimInsuredName") var claimInsuredName: String? = null,
    @SerializedName("claimInsuredPhone") var claimInsuredPhone: String? = null,
    @SerializedName("claimInsuredState") var claimInsuredState: String? = null,
    @SerializedName("claimInsuredZipCode") var claimInsuredZipCode: String? = null,
    @SerializedName("claimNumber") var claimNumber: String? = null,
    @SerializedName("claimPolicyNumber") var claimPolicyNumber: String? = null,
    @SerializedName("claimRepresentative") var claimRepresentative: String? = null,
    @SerializedName("contactDate") var contactDate: String? = null,
    @SerializedName("contractPhone") var contractPhone: String? = null,
    @SerializedName("creationDate") var creationDate: String? = null,
    @SerializedName("damageCause") var damageCause: String? = null,
    @SerializedName("damageDate") var damageDate: String? = null,
    @SerializedName("damageDescription") var damageDescription: String? = null,
    @SerializedName("damageType") var damageType: String? = null,
    @SerializedName("dateOfLoss") var dateOfLoss: String? = null,
    @SerializedName("dayPhone") var dayPhone: String? = null,
    @SerializedName("deleted") var deleted: Boolean? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("emailText") var emailText: String? = null,
    @SerializedName("homeOwner") var homeOwner: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("lossDescription") var lossDescription: String? = null,
    @SerializedName("lossType") var lossType: String? = null,
    @SerializedName("owner") var owner: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("projectNumber") var projectNumber: String? = null,
    @SerializedName("shortenedLinkToFileManager") var shortenedLinkToFileManager: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("structures") var structure: HashMap<String, Structure>? = null,
    @SerializedName("title") var title: String? = null
) : Parcelable