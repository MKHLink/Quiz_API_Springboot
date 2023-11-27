package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuizMapper quizMapper;

	private final QuestionRepository questionRepository;
	private final QuestionMapper questionMapper;

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.entitiesToDtos(quizRepository.findAll());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {


		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizMapper.requestDtoToEntity(quizRequestDto)));

	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		Optional<Quiz> quiz = quizRepository.findById(id);
		Quiz quizToDelete = quiz.get();
		quizRepository.delete(quizToDelete);

		return quizMapper.entityToDto(quizToDelete);
	}

	@Override
	public QuizResponseDto updateQuizName(Long id, String newname) {
		Quiz quizToUpdate = quizRepository.getById(id);
		quizToUpdate.setName(newname);
		quizRepository.save(quizToUpdate);
		return quizMapper.entityToDto(quizToUpdate);
	}

	@Override
	public QuestionResponseDto randomQuiz(Long id) {
		Object[] question = questionRepository.findByQuizId(id).toArray();
		Quiz quiz = quizRepository.getById(id);
		Question questionToPrint =(Question) question[new Random().nextInt(quiz.getQuestions().size())];
		return questionMapper.entityToDto(questionToPrint);
	}

	@Override
	public QuizResponseDto modifiedQuiz(Long id, QuestionRequestDto questionRequestDto) {
		Quiz quizToUpdate = quizRepository.getById(id);
		Question newQuestion = questionMapper.questionDtoToEntity(questionRequestDto);
		newQuestion.setQuiz(quizToUpdate);
		Question savedQuestion = questionRepository.saveAndFlush(newQuestion);
		quizToUpdate.getQuestions().add(savedQuestion);
		Quiz updatedQuiz = quizRepository.saveAndFlush(quizToUpdate);
		return quizMapper.entityToDto(updatedQuiz);
	}

	@Override
	public QuestionResponseDto deleteQuestion(Long id, Long question_id) {
		Optional<Question> question = questionRepository.findByIdAndQuizId(question_id, id);
		Question questionToDelete = question.get();
		questionRepository.delete(questionToDelete);
		return questionMapper.entityToDto(questionToDelete);
	}

}
