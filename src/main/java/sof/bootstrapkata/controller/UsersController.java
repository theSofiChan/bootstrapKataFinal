package sof.bootstrapkata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sof.bootstrapkata.entity.Role;
import sof.bootstrapkata.entity.User;
import sof.bootstrapkata.repository.UserRepository;
import sof.bootstrapkata.service.UserService;

import java.util.List;

@Controller
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class UsersController {
    private final UserRepository userRepo;
    private final UserService userService;

    @Autowired
    public UsersController(UserRepository userRepo, UserService userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }


    @PostMapping("admin/save")
    public String save(@ModelAttribute("user") User u, Model model){
        model.addAttribute("user",u);
        userRepo.save(u);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete")
    public String delete(User u){
        userRepo.deleteById(u.getId());
        return "redirect:/admin";
    }

    @GetMapping("/admin/findOne")
    @ResponseBody
    public User findOne(Long id){
        return userRepo.findById(id).orElse(null);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String currentUserName(Authentication authentication, Model model) {
        model.addAttribute("user", userService.get(userService.findByUserEmail(authentication.getName()).getId()));
        return "user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String currentAdminName(Authentication authentication, Model model) {
        model.addAttribute("user", userService.get(userService.findByUserEmail(authentication.getName()).getId()));
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("listRoles", listRoles);
        return "admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/new")
    public String newPerson(@ModelAttribute("user1") User user, Model model, Authentication authentication) {
        model.addAttribute("user", userService.get(userService.findByUserEmail(authentication.getName()).getId()));
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("user1", user);
        model.addAttribute("listRoles", listRoles);

        return "new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/new")
    public String create(@ModelAttribute("user") User user) {
        userService.update(user);
        return "redirect:/admin";
    }
}
