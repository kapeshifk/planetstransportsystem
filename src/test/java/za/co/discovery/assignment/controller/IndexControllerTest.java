package za.co.discovery.assignment.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by Kapeshi.Kongolo on 2016/04/19.
 */
public class IndexControllerTest {
    @Mock
    View mockView;
    @InjectMocks
    private IndexController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controller)
                .setSingleView(mockView)
                .build();

    }

    @Test
    public void verifyThatIndexViewIsCorrect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void verifyThatErrorPageViewIsCorrect() throws Exception {
        String message = "Failed to load the page. Please restart again.";
        mockMvc.perform(get(controller.getErrorPath()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", message))
                .andExpect(view().name("validation"));
    }

}