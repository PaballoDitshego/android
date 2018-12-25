package za.co.moxomo.model

import org.joda.time.DateTime
import org.parceler.Parcel
import org.parceler.ParcelPropertyConverter

import za.co.moxomo.helpers.JodaDateTimeConverter


@Parcel
class Vacancy {


    var id: Long? = null
    var job_title: String
    var description: String
    var company_name: String
    var location: String
    var min_qual: String
    var duties: String

    @ParcelPropertyConverter(JodaDateTimeConverter::class)
    var advertDate: DateTime
    @ParcelPropertyConverter(JodaDateTimeConverter::class)
    var closingDate: DateTime
    var website: String
    var imageUrl: String


}
