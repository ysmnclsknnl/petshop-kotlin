package com.example.petshopkotlin.unittests

import com.example.petshopkotlin.collection.User
import org.mockito.ArgumentMatcher

class UserMatcher(private val expectedUser: User) : ArgumentMatcher<User> {
    override fun matches(actualUser: User): Boolean {
        return expectedUser.userName == actualUser.userName &&
                expectedUser.password == actualUser.password &&
                expectedUser.role == actualUser.role
    }
}
