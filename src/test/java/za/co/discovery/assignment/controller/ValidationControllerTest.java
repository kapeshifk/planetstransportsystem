package za.co.discovery.assignment.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by Kapeshi.Kongolo on 2016/04/19.
 */
public class ValidationControllerTest {
    @Mock
    View mockView;
    @Mock
    private ErrorAttributes errorAttributes;
    @InjectMocks
    private ValidationController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(
                new ValidationController(errorAttributes)
        ).setViewResolvers(getInternalResourceViewResolver())
                .build();

    }

    private InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Test
    public void verifyThatValidationPageViewIsCorrect() throws Exception {
        String message = "The application has encountered an error. Please restart again.";
        mockMvc.perform(get(controller.getErrorPath()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", message))
                .andExpect(view().name("validation"));
    }
}