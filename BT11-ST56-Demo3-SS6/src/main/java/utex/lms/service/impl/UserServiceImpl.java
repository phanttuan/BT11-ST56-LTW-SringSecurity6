package utex.lms.service.impl;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utex.lms.entity.Role;
import utex.lms.entity.Users;
import utex.lms.model.SignUpDto;
import utex.lms.repository.RoleRepository;
import utex.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import utex.lms.service.MyUserService;

@Service
@Primary
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userRepository.getUserByUsername(username);
		if (user == null) {
			// fallback: try email
			user = userRepository.findByEmail(username).orElse(null);
		}
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user");
		}
		return new MyUserService(user);
	}

	public ResponseEntity<?> registerUser(SignUpDto signUpDto) {

		// add check for username exists in a DB
		if (userRepository.existsByUsername(signUpDto.getUsername())) {
			return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
		}

		// add check for email exists in DB
		if (userRepository.existsByEmail(signUpDto.getEmail())) {
			return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
		}

		// create user object
		Users user = new Users();
		user.setName(signUpDto.getName());
		user.setUsername(signUpDto.getUsername());
		user.setEmail(signUpDto.getEmail());
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

		Optional<Role> opt = roleRepository.findByName("USER");
		Role userRole = opt.orElseGet(() -> {
			Role r = new Role();
			r.setName("USER");
			return roleRepository.save(r);
		});
		user.setRoles(Collections.singleton(userRole));

		userRepository.save(user);

		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
	}
}