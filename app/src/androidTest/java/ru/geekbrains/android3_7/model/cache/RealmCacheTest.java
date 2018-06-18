package ru.geekbrains.android3_7.model.cache;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.realm.Realm;
import ru.geekbrains.android3_7.model.entity.Repository;
import ru.geekbrains.android3_7.model.entity.User;
import ru.geekbrains.android3_7.model.entity.realm.RealmRepository;
import ru.geekbrains.android3_7.model.entity.realm.RealmUser;

import static org.junit.Assert.assertEquals;

public class RealmCacheTest {

    static ICache realmCache;
    private static User userTest;
    private static List<Repository> repositoryList;

    @BeforeClass
    public static void createEntities() {
        userTest = new User("login_test", "avatarUrl_test");
        repositoryList = new ArrayList<>
                (Arrays.asList(new Repository("123456", "repo_test"), new Repository("123", "repo_test2")));
        realmCache = new RealmCache();
    }

    @Test
    public void putAndGetUser() {
        realmCache.putUser(userTest);
        TestObserver<User> testObserver = new TestObserver<>();
        Observable<User> userObservable = realmCache.getUser(userTest.getLogin());
        userObservable.subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);

        assertEquals(testObserver.values().get(0).getLogin(), userTest.getLogin());
        assertEquals(testObserver.values().get(0).getAvatarUrl(), userTest.getAvatarUrl());

        deleteUserAndReposFromRealm(userTest);
    }

    @Test
    public void putAndGetRepositories() {
        realmCache.putUserRepos(userTest, repositoryList);
        TestObserver<List<Repository>> testObserver = new TestObserver<>();
        Observable<List<Repository>> repoObservable = realmCache.getUserRepos(userTest);
        repoObservable.subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);

        assertEquals(testObserver.values().get(0).get(0).getId(), repositoryList.get(0).getId());
        assertEquals(testObserver.values().get(0).get(0).getName(), repositoryList.get(0).getName());

        deleteUserAndReposFromRealm(userTest);
    }

    public void deleteUserAndReposFromRealm(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmUser realmUser = realm1.where(RealmUser.class).equalTo("login", userTest.getLogin()).findFirst();
            List<RealmRepository> repositoryList = realmUser.getRepositories();
            if (repositoryList != null && !repositoryList.isEmpty()) {
                realmUser.getRepositories().deleteAllFromRealm();
            }
            realmUser.deleteFromRealm();
        });
        realm.close();

    }
}