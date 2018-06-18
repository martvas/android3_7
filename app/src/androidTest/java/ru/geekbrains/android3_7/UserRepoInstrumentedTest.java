package ru.geekbrains.android3_7;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import ru.geekbrains.android3_7.di.DaggerTestComponent;
import ru.geekbrains.android3_7.di.TestComponent;
import ru.geekbrains.android3_7.di.modules.ApiModule;
import ru.geekbrains.android3_7.model.entity.Repository;
import ru.geekbrains.android3_7.model.entity.User;
import ru.geekbrains.android3_7.model.repo.UsersRepo;

import static org.junit.Assert.assertEquals;

public class UserRepoInstrumentedTest {
    private static MockWebServer mockWebServer;
    @Inject
    UsersRepo usersRepo;

    @BeforeClass
    public static void setupClass() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        mockWebServer.shutdown();
    }

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent
                .builder()
                .apiModule(new ApiModule() {
                    @Override
                    public String endpoint() {
                        return mockWebServer.url("/").toString();
                    }
                }).build();

        component.inject(this);
    }

    @After
    public void tearDown() {

    }


    @Test
    public void getUser() {
        mockWebServer.enqueue(createUserResponse("somelogin", "someurl"));

        TestObserver<User> observer = new TestObserver<>();
        usersRepo.getUser("somelogin").subscribe(observer);

        observer.awaitTerminalEvent();
        observer.assertValueCount(1);

        assertEquals(observer.values().get(0).getLogin(), "somelogin");
        assertEquals(observer.values().get(0).getAvatarUrl(), "someurl");
    }

    private MockResponse createUserResponse(String login, String avatarUrl) {
        String body = "{\"login\":\"" + login + "\", \"avatar_url\":\"" + avatarUrl + "\"}";
        return new MockResponse()
                .setBody(body);
    }

    @Test
    public void getRepos() {
        User testUser = new User("chuvak", "chuvak_url");
        mockWebServer.enqueue(createReposResponse());

        TestObserver<List<Repository>> observer = new TestObserver<>();
        usersRepo.getUserRepos(testUser).subscribe(observer);

        observer.awaitTerminalEvent();
        observer.assertValueCount(1);

        assertEquals(observer.values().get(0).get(0).getId(), "42662933");
        assertEquals(observer.values().get(0).get(0).getName(), "C45algorithm");
    }

    private MockResponse createReposResponse() {
        return new MockResponse()
                .setBody(JsonExample.jsonRepos);
    }
}
