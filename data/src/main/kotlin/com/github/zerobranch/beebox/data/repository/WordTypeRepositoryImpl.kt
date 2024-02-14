package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.data.base.NetworkErrorParser
import com.github.zerobranch.beebox.data.entity.WordTypeEntity
import com.github.zerobranch.beebox.data.mappers.toDomain
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.repository.WordTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class WordTypeRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val networkErrorParser: NetworkErrorParser
) : WordTypeRepository {

    override fun create(name: String, description: String): Flow<Unit> =
        flow {
            emit(
                appDatabase.wordTypesDao().insert(
                    WordTypeEntity(
                        typeId = System.currentTimeMillis(),
                        name = name,
                        description = description,
                        createdDate = ZonedDateTime.now()
                    )
                )
            )
        }.catch { networkErrorParser.invoke(it) }

    override fun getAll(): Flow<List<WordType>> =
        appDatabase.wordTypesDao().readAllLife()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllCrossRefs(): Flow<Map<String, Long>> =
        appDatabase.wordTypesDao().readAllCrossRefsLife()
            .map { entities -> entities.associate { it.word to it.typeId } }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllWithRelations(): Flow<Map<Long, Int>> = flow {
        val allTypes = appDatabase.wordTypesDao().readAll()
        val wordDao = appDatabase.wordDao()
        emit(allTypes.associate { it.typeId to wordDao.getWordCountByType(it.typeId) })
    }.catch { networkErrorParser.invoke(it) }

    override fun containsWordsIsType(wordTypeId: Long): Flow<Boolean> {
        return flow {
            emit(appDatabase.wordTypesDao().getWordsIdsByType(wordTypeId).isNotEmpty())
        }.catch { networkErrorParser.invoke(it) }
    }

    override fun delete(wordType: WordType): Flow<Unit> =
        flow {
            appDatabase.runInTransaction {
                appDatabase.wordTypesDao().deleteWordsFromType(wordType.id)
                appDatabase.wordTypesDao().delete(wordType.id)
            }
            emit(Unit)
        }.catch { networkErrorParser.invoke(it) }

    override fun change(id: Long, name: String, description: String): Flow<Unit> =
        flow { emit(appDatabase.wordTypesDao().update(id, name, description)) }
            .catch { networkErrorParser.invoke(it) }
}