package hu.adamsan.gitdb

import hu.adamsan.gitdb.commands.Help
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class GitdbApplicationTests {

	@Autowired
	private var app: GitdbApplication = GitdbApplication()

//	@MockBean
//	private var help = mock(Help::class.java)

	@Test
	fun test_app_prints_help() {

		val args = DefaultApplicationArguments("help")
		app.run(args)
		//verify(help).run()
	}

}
