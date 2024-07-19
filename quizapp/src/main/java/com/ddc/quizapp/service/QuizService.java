package com.ddc.quizapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ddc.quizapp.dao.QuestionDao;
import com.ddc.quizapp.dao.QuizDao;
import com.ddc.quizapp.model.Question;
import com.ddc.quizapp.model.QuestionWrapper;
import com.ddc.quizapp.model.Quiz;
import com.ddc.quizapp.model.Response;

@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQues, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQues);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);

        quizDao.save(quiz);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer quiz_id) {
        Optional<Quiz> quiz = quizDao.findById(quiz_id);
        List<Question> questionsFromDB = quiz.get().getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();

        for (Question q : questionsFromDB) {
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());

            questionsForUser.add(qw);
        }

        return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Optional<Quiz> quiz = quizDao.findById(id);
        List<Question> questionsFromDB = quiz.get().getQuestions();

        // Sort responses based on id
        responses.sort(Comparator.comparing(Response::getId));

        // Sort questionsFromDB based on id
        questionsFromDB.sort(Comparator.comparing(Question::getId));
        
        int rightAnswers = 0;
        int i = 0;

        for (Response response : responses) {
            if (response.getResponse().equals(questionsFromDB.get(i).getRightAnswer())) {
                rightAnswers++;
            }

            i++;
        }

        return new ResponseEntity<>(rightAnswers, HttpStatus.OK);
    }
}
