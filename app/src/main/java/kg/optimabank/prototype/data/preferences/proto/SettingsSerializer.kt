package kg.optimabank.prototype.data.preferences.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import kg.optimabank.prototype.dev.UserSettings
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object SettingsSerializer : Serializer<UserSettings> {

    private const val FILE_NAME = "files.pb"

    override val defaultValue: UserSettings = UserSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSettings {
        return try {
            UserSettings.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            Timber.e("Cannot read proto. Create default. ${ex.localizedMessage}")
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) = t.writeTo(output)

    val Context.settingsDataStore: DataStore<UserSettings> by dataStore(
        fileName = FILE_NAME,
        serializer = SettingsSerializer
    )
}