package com.ylabz.basepro.ashbike.wear.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService

@OptIn(ExperimentalHorologistApi::class)
class AshBikeTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ) = ResourceBuilders.Resources.Builder().setVersion("1").build()

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {

        // 1. Mock Data
        val speed = "22.5"

        // 2. Build Layout
        val rootLayout = PrimaryLayout.Builder(requestParams.deviceConfiguration)
            .setResponsiveContentInsetEnabled(true)
            .setContent(
                // FIX: Use LayoutElementBuilders for the Column
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        // Use Material for the Text
                        Text.Builder(this, "LAST RIDE")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(argb(0xFFAAAAAA.toInt()))
                            .build()
                    )
                    .addContent(
                        Text.Builder(this, speed)
                            .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                            .setColor(argb(0xFFFFD700.toInt())) // AshBike Gold
                            .build()
                    )
                    .addContent(
                        Text.Builder(this, "km/h")
                            .setTypography(Typography.TYPOGRAPHY_BODY2)
                            .setColor(argb(0xFFFFFFFF.toInt()))
                            .build()
                    )
                    .build()
            )
            .setPrimaryChipContent(
                CompactChip.Builder(
                    this,
                    "GO",
                    // FIX: Use ModifiersBuilders for the Clickable
                    ModifiersBuilders.Clickable.Builder()
                        .setOnClick(
                            ActionBuilders.LaunchAction.Builder()
                                .setAndroidActivity(
                                    ActionBuilders.AndroidActivity.Builder()
                                        .setClassName("com.ylabz.basepro.ashbike.wear.app.MainActivity")
                                        .setPackageName(packageName)
                                        .build()
                                ).build()
                        ).build(),
                    requestParams.deviceConfiguration
                ).build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder().addTimelineEntry(
                    TimelineBuilders.TimelineEntry.Builder()
                        .setLayout(
                            LayoutElementBuilders.Layout.Builder().setRoot(rootLayout).build()
                        ).build()
                ).build()
            ).build()
    }
}