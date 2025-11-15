package id.app.todoschedule.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.app.todoschedule.data.local.AppDatabase
import id.app.todoschedule.data.local.dao.EntryDao
import id.app.todoschedule.data.local.dao.ReminderDao
import id.app.todoschedule.data.local.dao.TagDao
import javax.inject.Singleton

/**
 * Hilt Module untuk Database dan DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // TODO: Remove in production, add proper migrations
            .build()
    }

    @Provides
    @Singleton
    fun provideEntryDao(database: AppDatabase): EntryDao {
        return database.entryDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }
}
