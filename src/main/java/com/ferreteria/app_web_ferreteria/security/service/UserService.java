package com.ferreteria.app_web_ferreteria.security.service;


import com.ferreteria.app_web_ferreteria.security.dto.ApiResponse;
import com.ferreteria.app_web_ferreteria.security.dto.JwtDto;
import com.ferreteria.app_web_ferreteria.security.dto.LoginUser;
import com.ferreteria.app_web_ferreteria.security.dto.RegisterUser;
import com.ferreteria.app_web_ferreteria.security.entity.Rol;
import com.ferreteria.app_web_ferreteria.security.entity.User;
import com.ferreteria.app_web_ferreteria.security.enums.RolNombre;
import com.ferreteria.app_web_ferreteria.security.exceptions.CustomException;
import com.ferreteria.app_web_ferreteria.security.jwt.JwtProvider;
import com.ferreteria.app_web_ferreteria.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    public Optional<User> getByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getByIdUser(Long id){
        return userRepository.findById(id);
    }

    public ApiResponse updateState(Long id, boolean state){
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.ifPresent(user -> {
            user.setActive(state);
            userRepository.save(user);
        });

        return optionalUser.map(user -> new ApiResponse("Se cambió el estado de " + user.getName()))
                .orElse(new ApiResponse("No se encontró usuario con el id " + id));
    }

    public JwtDto login(LoginUser loginUser) {
        LOGGER.info("Iniciando sesión para el usuario: {}", loginUser.getUsername());

        JwtDto jwtDto = null;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<RolNombre> roles = user.getRoles().stream()
                        .map(Rol::getRolName)
                        .collect(Collectors.toList());
                String jwt = jwtProvider.generateToken(authentication);
                String fullName = user.getName() + " " + user.getLastname();
                Long id = user.getId();

                jwtDto = new JwtDto(jwt, roles, fullName, id);

                LOGGER.info("Inicio de sesión exitoso para el usuario: {}", loginUser.getUsername());
            } else {
                LOGGER.warn("Usuario no encontrado: {}", loginUser.getUsername());
                throw new UsernameNotFoundException("Usuario no encontrado: " + loginUser.getUsername());
            }
        } catch (AuthenticationException e) {
            LOGGER.error("Error en la autenticación para el usuario: {}", loginUser.getUsername(), e);
            throw e;
        }

        return jwtDto;
    }

    public JwtDto refresh(String token) throws ParseException {
        LOGGER.info("Refrescando token de acceso : {}", token);

        String tokens = jwtProvider.refreshToken(token);

        LOGGER.info("Token de acceso refrescado : {}", tokens);
        return new JwtDto(token);
    }

    public ApiResponse save(RegisterUser registerUser){
        LOGGER.info("Guardando nuevo usuario: {}", registerUser.getId());

        ApiResponse apiResponse = null;

        if(registerUser.getId() != null){
            apiResponse = updateUser(registerUser);
        }else{
            apiResponse = saveUser(registerUser);
        }

        return apiResponse;
    }

    public ApiResponse saveUser(RegisterUser registerUser){

        if(userRepository.existsByUsername(registerUser.getUsername())) {
            LOGGER.warn("Nombre de usuario {} ya existe", registerUser.getUsername());
            throw new CustomException(HttpStatus.BAD_REQUEST, "Ese nombre de usuario ya existe");
        }
        if(userRepository.existsByEmail(registerUser.getEmail())) {
            LOGGER.warn("Correo electrónico {} ya existe", registerUser.getEmail());
            throw new CustomException(HttpStatus.BAD_REQUEST, "Ese correo electrónico ya está en uso");
        }

        User user = new User(registerUser.getName(), registerUser.getLastname(), registerUser.getUsername(), registerUser.getEmail(),
                passwordEncoder.encode(registerUser.getPassword()), registerUser.getIsActive(), registerUser.getDocument());

        Set<Rol> roles = new HashSet<>();

        if (registerUser.getRoles().contains("ADMINISTRADOR")) {
            roles.add(rolService.getByRolName(RolNombre.ADMINISTRADOR)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Rol no encontrado")));
        }

        if (registerUser.getRoles().contains("VENDEDOR")) {
            roles.add(rolService.getByRolName(RolNombre.VENDEDOR)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Rol no encontrado")));
        }

        user.setRoles(roles);

        userRepository.save(user);

        LOGGER.info("Usuario {} creado exitosamente", registerUser.getUsername());
        return new ApiResponse(user.getUsername() + " ha sido creado");
    }

    public ApiResponse updateUser(RegisterUser registerUser) {
        LOGGER.info("Actualizando usuario existente: {}", registerUser.getId());

        User existingUser = userRepository.findById(registerUser.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!Objects.equals(registerUser.getPassword(), existingUser.getPassword())) {
            System.out.println("jojo son distrintos: "+registerUser.getPassword());

            String encryptedPassword = passwordEncoder.encode(registerUser.getPassword());
            existingUser.setPassword(encryptedPassword);
        }

        existingUser.setName(registerUser.getName());
        existingUser.setLastname(registerUser.getLastname());
        existingUser.setEmail(registerUser.getEmail());
        existingUser.setActive(registerUser.getIsActive());
        existingUser.setDocument(registerUser.getDocument());
        existingUser.setUsername(registerUser.getUsername());

        Set<Rol> existingRoles = existingUser.getRoles();

        Set<String> providedRoles = registerUser.getRoles();

        providedRoles.forEach(roleName -> {
            Rol role = rolService.getByRolName(RolNombre.valueOf(roleName))
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
            existingRoles.add(role);
        });

        existingRoles.removeIf(role -> !providedRoles.contains(role.getRolName().toString()));

        userRepository.save(existingUser);

        LOGGER.info("Usuario {} actualizado exitosamente", existingUser.getUsername());
        return new ApiResponse(existingUser.getUsername() + " ha sido actualizado");
    }


    public User findByCorreoElectronico(String correoElectronico) {
        return userRepository.findByCorreoElectronico(correoElectronico);
    }

  

    


    public Page<User> findAllUsuarioss(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public Page<User> listarUsuariosEstado(boolean estado, Pageable pageable) {

        List<User> usuarios = userRepository.findByIsActiveOrderByIdDesc(estado);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> sublist;

        if (usuarios.size() < startItem) {
            sublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, usuarios.size());
            sublist = usuarios.subList(startItem, toIndex);
        }

        return new PageImpl<>(sublist, pageable, usuarios.size());
    }


    public Page<User> filtrarUsuarios(String textoBuscar, Pageable pageable) {

        List<User> usuarios = userRepository.searchUsuario(textoBuscar);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> sublist;

        if (usuarios.size() < startItem) {
            sublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, usuarios.size());
            sublist = usuarios.subList(startItem, toIndex);
        }

        return new PageImpl<>(sublist, pageable, usuarios.size());
    }

    public boolean existByDocumento(String document) {
        return userRepository.existsByDocument(document);
    }

    public List<User> getActiveVendedores() {
        return userRepository.findActiveVendedores();
    }
}
