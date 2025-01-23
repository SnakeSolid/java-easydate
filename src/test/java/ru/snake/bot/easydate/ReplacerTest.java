package ru.snake.bot.easydate;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.snake.bot.easydate.conversation.text.Replacer;

public class ReplacerTest {

	@Test
	public void mustReturnEmpty() {
		String result = Replacer.replace("", Map.of());

		Assertions.assertEquals("", result);
	}

	@Test
	public void mustReturnSingleParameter() {
		String result = Replacer.replace("{one}", Map.of("one", "one"));

		Assertions.assertEquals("one", result);
	}

	@Test
	public void mustReturnTwoParameters() {
		String result = Replacer.replace("{one}{two}", Map.of("one", "one", "two", "two"));

		Assertions.assertEquals("onetwo", result);
	}

	@Test
	public void mustReturnEmptyText() {
		String result = Replacer.replace("{three}", Map.of("one", "one", "two", "two"));

		Assertions.assertEquals("", result);
	}

	@Test
	public void mustReturnSubstitutedText1() {
		String result = Replacer.replace("the {one} text", Map.of("one", "substitution"));

		Assertions.assertEquals("the substitution text", result);
	}

	@Test
	public void mustReturnSubstitutedText2() {
		String result = Replacer.replace("{one} substitution {two}", Map.of("one", "the", "two", "text"));

		Assertions.assertEquals("the substitution text", result);
	}

}
