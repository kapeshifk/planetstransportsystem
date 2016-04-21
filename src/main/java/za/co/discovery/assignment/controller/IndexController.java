package za.co.discovery.assignment.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping(value = PATH)
    public String error(Model model) {
        String message = "Failed to load the page. Please restart again.";
        model.addAttribute("validationMessage", message);
        return "validation";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
