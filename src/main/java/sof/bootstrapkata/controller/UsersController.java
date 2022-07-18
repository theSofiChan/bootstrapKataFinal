package sof.bootstrapkata.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersController(UserRepository userRepo, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("admin/save")
    public String saveUser(@ModelAttribute("user") User u, Model model){
        String password = passwordEncoder.encode(u.getPassword());
        u.setPassword(password);
        model.addAttribute("user",u);
        userRepo.save(u);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/delete")
    public String deleteUser(User u){
        String password = passwordEncoder.encode(u.getPassword());
        u.setPassword(password);
        userRepo.deleteById(u.getId());
        return "redirect:/admin";
    }



    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String goToUserPage(Authentication authentication, Model model) {
        User user=userService.get(userService.findByUserEmail(authentication.getName()).getId());
        model.addAttribute("user",user);
        return "user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String goToAdminPage(Authentication authentication, Model model) {
        User user=userService.get(userService.findByUserEmail(authentication.getName()).getId());
        model.addAttribute("user", user);
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("listRoles", listRoles);
        return "admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/new")
    public String createNewPersonView(@ModelAttribute("user1") User user, Model model, Authentication authentication) {
        model.addAttribute("user", userService.get(userService.findByUserEmail(authentication.getName()).getId()));
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("user1", user);
        model.addAttribute("listRoles", listRoles);

        return "new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/new")
    public String createNewUser(@ModelAttribute("user") User user) {
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        userService.update(user);
        return "redirect:/admin";
    }
}
