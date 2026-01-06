package com.ylabz.basepro.ashbike.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.ylabz.basepro.ashbike.wear.R
import com.ylabz.basepro.ashbike.wear.presentation.MainActivity

class AshBikeComplicationService : SuspendingComplicationDataSourceService() {

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        // 1. Prepare the Click Action (Open App)
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Mock Data (Later, inject your Repository here to get "Last Ride")
        val distance = "12.4"
        val unit = "km"

        // 3. Return the correct template based on what the Watch Face asks for
        return when (request.complicationType) {

            // A. SHORT TEXT (The small circles) -> Shows "12.4"
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(distance).build(),
                contentDescription = PlainComplicationText.Builder("Last Ride Distance").build()
            )
                .setTitle(PlainComplicationText.Builder(unit).build()) // Shows "km" above/below
                .setMonochromaticImage(
                    MonochromaticImage.Builder(
                        Icon.createWithResource(this, R.drawable.ic_launcher_foreground) // Use a bike icon here!
                    ).build()
                )
                .setTapAction(pendingIntent)
                .build()

            // B. LONG TEXT (The wide bars) -> Shows "Last Ride: 12.4 km"
            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Last Ride: $distance $unit").build(),
                contentDescription = PlainComplicationText.Builder("Last Ride Stat").build()
            )
                .setMonochromaticImage(
                    MonochromaticImage.Builder(
                        Icon.createWithResource(this, R.drawable.ic_launcher_foreground)
                    ).build()
                )
                .setTapAction(pendingIntent)
                .build()

            // C. SMALL IMAGE (Just the Icon)
            ComplicationType.SMALL_IMAGE -> {
                // 1. Create the SmallImage object wrapper first
                val smallImage = androidx.wear.watchface.complications.data.SmallImage.Builder(
                    image = Icon.createWithResource(this, R.drawable.ic_bike), // Your Icon
                    type = SmallImageType.ICON
                ).build()

                // 2. Create the Complication Data using that wrapper
                SmallImageComplicationData.Builder(
                    smallImage = smallImage, // <--- Correct parameter
                    contentDescription = PlainComplicationText.Builder("Open AshBike").build()
                )
                    .setTapAction(pendingIntent)
                    .build()
            }

            else -> null // Type not supported
        }
    }

    // This data is shown in the "Picker" when the user is choosing a complication
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("0.0").build(),
                contentDescription = PlainComplicationText.Builder("Preview").build()
            ).setTitle(PlainComplicationText.Builder("km").build()).build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Ride: 0.0 km").build(),
                contentDescription = PlainComplicationText.Builder("Preview").build()
            ).build()

            else -> null
        }
    }
}