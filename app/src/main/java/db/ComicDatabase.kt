package db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jpobi.xkcd_viewer.Comic

@Database(entities = [Comic::class], version = 1)
abstract class ComicDataBase: RoomDatabase() {
    abstract val comicDao: ComicDao
}

private lateinit var INSTANCE: ComicDataBase

fun getDataBase(context: Context): ComicDataBase {
    synchronized(ComicDataBase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ComicDataBase::class.java,
                "comic_db"
            ).build()
        }
    }
    return INSTANCE
}