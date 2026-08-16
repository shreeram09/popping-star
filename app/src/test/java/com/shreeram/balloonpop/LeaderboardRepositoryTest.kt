package com.shreeram.balloonpop

import com.shreeram.balloonpop.leaderboard.LeaderboardDao
import com.shreeram.balloonpop.leaderboard.LeaderboardEntry
import com.shreeram.balloonpop.leaderboard.LeaderboardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LeaderboardRepositoryTest {
    private val leaderboardDao = mockk<LeaderboardDao>(relaxed = true)
    private val repository = LeaderboardRepository(leaderboardDao)

    @Test
    fun `adds a score when the profile has no leaderboard entry`() = runTest {
        val entry = LeaderboardEntry(profileId = "alice", playerName = "Alice", score = 25)
        coEvery { leaderboardDao.getEntryForProfile("alice") } returns null
        coEvery { leaderboardDao.insertEntry(entry) } returns Unit

        repository.addScore(entry)

        coVerify(exactly = 1) { leaderboardDao.insertEntry(entry) }
    }

    @Test
    fun `keeps the existing score when a lower score is submitted`() = runTest {
        val existingEntry = LeaderboardEntry(profileId = "alice", playerName = "Alice", score = 25)
        val lowerEntry = existingEntry.copy(score = 10)
        coEvery { leaderboardDao.getEntryForProfile("alice") } returns existingEntry

        repository.addScore(lowerEntry)

        coVerify(exactly = 0) { leaderboardDao.insertEntry(any()) }
    }

    @Test
    fun `replaces the entry when an equal or higher score is submitted`() = runTest {
        val existingEntry = LeaderboardEntry(profileId = "alice", playerName = "Alice", score = 25)
        val higherEntry = existingEntry.copy(score = 30)
        coEvery { leaderboardDao.getEntryForProfile("alice") } returns existingEntry
        coEvery { leaderboardDao.insertEntry(higherEntry) } returns Unit

        repository.addScore(higherEntry)

        coVerify(exactly = 1) { leaderboardDao.insertEntry(higherEntry) }
    }
}