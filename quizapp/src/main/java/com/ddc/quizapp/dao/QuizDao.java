package com.ddc.quizapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddc.quizapp.model.Quiz;


public interface QuizDao extends JpaRepository<Quiz, Integer>{

}
