package kg.optimabank.prototype.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import kg.optimabank.prototype.data.database.OptimaDatabase
import kg.optimabank.prototype.data.preferences.*
import javax.inject.Singleton

@Module
class StorageModule(private val context: Context) {

    companion object {
        const val DATABASE_NAME = "optima_database_1"
    }

    @Singleton
    @Provides
    fun provideDataSoreManager(): DataStoreManager = DataStoreManager(context)

    @Singleton
    @Provides
    fun provideSessionDataPreferences(dataStoreManager: DataStoreManager): SessionPreferences {
        return SessionPreferencesImpl(dataStoreManager)
    }

    @Singleton
    @Provides
    fun provideLoginPreferences(
        sessionPreferences: SessionPreferences,
        dataStoreManager: DataStoreManager
    ): LoginPreferences {
        return LoginPreferencesImpl(context, sessionPreferences, dataStoreManager)
    }

    @Singleton
    @Provides
    fun provideDatabase(): OptimaDatabase {
        return Room.databaseBuilder(
            context,
            OptimaDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}