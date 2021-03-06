package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.util.concurrent.FakeTimeLimiter
import com.udacity.project4.locationreminders.FakeData
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: FakeDataSource

    private lateinit var viewModel: RemindersListViewModel

    private lateinit var context: Application

    @Before
    fun setup() {
        stopKoin()
        context = ApplicationProvider.getApplicationContext()
        repository = FakeDataSource()
        viewModel = RemindersListViewModel(context, repository)
    }


    @Test
    fun loadRemindersVerifyListIsLoaded() = runBlockingTest {

        FakeData.reminders.forEach { r ->
            repository.saveReminder(r)
        }

        viewModel.loadReminders()

        assert(viewModel.remindersList.getOrAwaitValue().size == 3)
    }


    @Test
    fun loadRemindersNoItemsExistThenRemindersListIsEmpty() = runBlockingTest {
        viewModel.loadReminders()
        val existingReminders = viewModel.remindersList.getOrAwaitValue()
        assert(existingReminders.isEmpty())
    }

    @Test

    fun loadRemindersAndShowLoadingIsShown() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        viewModel.loadReminders()

        assert(viewModel.showLoading.getOrAwaitValue() == true)

        mainCoroutineRule.resumeDispatcher()

        assert(viewModel.showLoading.getOrAwaitValue() == false)
    }
}