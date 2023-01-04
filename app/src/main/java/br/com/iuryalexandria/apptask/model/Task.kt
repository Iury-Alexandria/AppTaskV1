package br.com.iuryalexandria.apptask.model

import android.os.Parcelable
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(

    var id: String = "",
    var description: String = "",
    var status: Int = 0
): Parcelable{
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
