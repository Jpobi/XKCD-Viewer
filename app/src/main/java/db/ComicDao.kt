package db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jpobi.xkcd_viewer.Comic

@Dao
interface ComicDao {

    @Query("SELECT * FROM comic")
    fun getAllComics(): MutableList<Comic>

    @Query("SELECT * FROM comic WHERE num= :id LIMIT 1")
    fun getComicById(id : String) : Comic

    @Query("SELECT * FROM comic WHERE isFav=true")
    fun getFavComics(): MutableList<Comic>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(comic: MutableList<Comic>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comic: Comic)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(response: Comic)
    @Update
    fun update(comic: Comic)

    @Delete
    fun delete(comic: Comic)
    @Query("SELECT num FROM comic")
    fun getIds(): List<Int>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrInsert(comic: Comic)

}