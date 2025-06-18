package com.iago.agenda_servicos.config.setup;

import com.iago.agenda_servicos.model.*;
import com.iago.agenda_servicos.repository.BarbeariaRepository;
import com.iago.agenda_servicos.repository.RoleRepository;
import com.iago.agenda_servicos.repository.ServicoRepository;
import com.iago.agenda_servicos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Este componente é executado na inicialização da aplicação para popular o banco de dados
 * com dados essenciais e de exemplo, facilitando o desenvolvimento e demonstrações.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final BarbeariaRepository barbeariaRepository;
    private final ServicoRepository servicoRepository;

    @Value("${app.super-admin.email}")
    private String superAdminEmail;

    @Value("${app.super-admin.senha}")
    private String superAdminSenha;

    public DataInitializer(RoleRepository roleRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, BarbeariaRepository barbeariaRepository, ServicoRepository servicoRepository) {
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.barbeariaRepository = barbeariaRepository;
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Criar Roles se não existirem
        if (roleRepository.count() == 0) {
            Arrays.stream(RoleName.values()).forEach(roleName -> roleRepository.save(new Role(roleName)));
        }

        // 2. Criar Super Admin se não existir
        if (!usuarioRepository.existsByEmail(superAdminEmail)) {
            criarSuperAdmin();
        }

        // 3. Criar dados de exemplo se o banco estiver "vazio" (apenas com o super admin)
        if (usuarioRepository.count() <= 1) {
            criarDadosDeExemplo();
        }
    }

    private void criarSuperAdmin() {
        Role superAdminRole = roleRepository.findByNome(RoleName.ROLE_SUPER_ADMIN)
                .orElseThrow(() -> new RuntimeException("Erro: Perfil SUPER_ADMIN não encontrado."));

        Usuario superAdmin = Usuario.builder()
                .nomeCompleto("Super Admin")
                .email(superAdminEmail)
                .senha(passwordEncoder.encode(superAdminSenha))
                .telefone("000000000")
                .roles(new HashSet<>(Set.of(superAdminRole)))
                .build();
        usuarioRepository.save(superAdmin);
    }

    private void criarDadosDeExemplo() {
        // Pegar Roles do banco
        Role ownerRole = roleRepository.findByNome(RoleName.ROLE_OWNER).orElseThrow();
        Role profissionalRole = roleRepository.findByNome(RoleName.ROLE_PROFISSIONAL).orElseThrow();
        Role clienteRole = roleRepository.findByNome(RoleName.ROLE_CLIENTE).orElseThrow();

        // Criar usuário DONO
        Usuario dono = Usuario.builder()
                .nomeCompleto("Iago Dono da Silva")
                .email("dono@barbearia.com")
                .senha(passwordEncoder.encode("senha123"))
                .telefone("19999998888")
                .roles(Set.of(ownerRole))
                .build();
        usuarioRepository.save(dono);

        // Criar BARBEARIA para o dono
        Barbearia barbearia = Barbearia.builder()
                .nome("Barbearia StyleMax")
                .endereco("Avenida Principal, 123, Centro")
                .horaAbertura(LocalTime.of(9, 0))
                .horaFechamento(LocalTime.of(20, 0))
                .owner(dono)
                .build();
        barbeariaRepository.save(barbearia);

        // Criar usuário PROFISSIONAL para a barbearia
        Usuario profissional = Usuario.builder()
                .nomeCompleto("Carlos Profissional")
                .email("carlos.prof@barbearia.com")
                .senha(passwordEncoder.encode("senha123"))
                .telefone("19777776666")
                .roles(Set.of(profissionalRole))
                .barbearia(barbearia) // Vincula à barbearia
                .percentualComissao(new BigDecimal("0.40")) // 40% de comissão
                .build();
        usuarioRepository.save(profissional);

        // Criar usuário CLIENTE
        Usuario cliente = Usuario.builder()
                .nomeCompleto("Bruno Cliente Fiel")
                .email("cliente@email.com")
                .senha(passwordEncoder.encode("senha123"))
                .telefone("19555554444")
                .roles(Set.of(clienteRole))
                .build();
        usuarioRepository.save(cliente);

        // Criar SERVIÇOS para a barbearia
        Servico servico1 = Servico.builder().nome("Corte Masculino").descricao("Corte moderno com tesoura e máquina.").duracaoMinutos(30).precoBase(new BigDecimal("45.00")).ativo(true).barbearia(barbearia).build();
        Servico servico2 = Servico.builder().nome("Barba Terapia").descricao("Modelagem de barba com toalha quente e massagem.").duracaoMinutos(45).precoBase(new BigDecimal("55.00")).ativo(true).barbearia(barbearia).build();
        Servico servico3 = Servico.builder().nome("Corte e Barba").descricao("Combo completo de corte e barba.").duracaoMinutos(75).precoBase(new BigDecimal("90.00")).ativo(true).barbearia(barbearia).build();
        servicoRepository.saveAll(Arrays.asList(servico1, servico2, servico3));
    }
}