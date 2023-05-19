package com.example.plugins.tutorial.jira.AO.ToDo;

import com.atlassian.activeobjects.tx.Transactional;

import java.util.List;

@Transactional
public interface TodoService {
    Todo add(String description);

    List<Todo> all();
}