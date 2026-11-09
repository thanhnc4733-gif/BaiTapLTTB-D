package com.example.baitap1tuan5

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.CalendarContract
import android.provider.ContactsContract
import androidx.core.app.NotificationCompat
import java.util.Calendar
import java.util.Locale

// Quyền THÔNG BÁO
object Notifier {
    private const val CHANNEL = "uth_todo"

    fun send(context: Context, title: String, body: String) {
        if (!context.isGranted(AppPermission.NOTIFICATION)) return
        val nm = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "UTH Todo", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        val pi = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), n)
    }
}

// Quyền VỊ TRÍ
@SuppressLint("MissingPermission")
fun currentPlace(context: Context): String? {
    return try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val loc = lm.getProviders(true)
            .mapNotNull { lm.getLastKnownLocation(it) }
            .maxByOrNull { it.time }
        loc?.let { "%.4f, %.4f".format(Locale.US, it.latitude, it.longitude) }
    } catch (e: SecurityException) {
        null
    }
}

// Quyền DANH BẠ
fun loadContacts(context: Context): List<String> {
    val list = mutableListOf<String>()
    try {
        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
            null, null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )?.use { c ->
            val i = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (c.moveToNext() && list.size < 30) {
                c.getString(i)?.let { list.add(it) }
            }
        }
    } catch (e: SecurityException) {
        // chưa có quyền
    }
    return list
}

// Quyền LỊCH
fun todayEventCount(context: Context): Int {
    return try {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, start.timeInMillis)
        ContentUris.appendId(builder, end.timeInMillis)
        context.contentResolver.query(
            builder.build(),
            arrayOf(CalendarContract.Instances.EVENT_ID),
            null, null, null
        )?.use { it.count } ?: 0
    } catch (e: SecurityException) {
        0
    }
}
