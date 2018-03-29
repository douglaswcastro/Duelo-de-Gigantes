package com.senai.controle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.senai.dao.UsuarioDao;
import com.senai.exception.UsuarioException;
import com.senai.modelo.Usuario;
import com.senai.util.StringUtils;

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Usuario usuario;
	private UsuarioDao dao;
       
    public UsuarioServlet() {
    	dao = new UsuarioDao();
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String acao = request.getParameter("acao");
		
		if ("sair".equals(acao)) {
			efetuarLogout(request, response); 
		}
		
		Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		if (usuario == null) {
			if ("salvar".equals(acao)) {
				salvarUsuario(request, response);
			} else if ("login".equals(acao)) {
				mostrarLogin(request, response);
			} else if ("logar".equals(acao)) {
				efetuarLogin(request, response);
			} else {
				mostrarCadastro(request, response);
			}
		}else {
			mostrarLogout(request, response);
		}
	}
	
	private void capturarParametros(HttpServletRequest request) throws ServletException, IOException, NoSuchAlgorithmException {
		Usuario u = new Usuario();
		
		String sid = request.getParameter("id");
		if (sid != null) {
			u.setId(Integer.parseInt(sid));
		}
		u.setNome(request.getParameter("nome"));
		u.setEmail(request.getParameter("email"));
		
		u.setSenha(request.getParameter("senha"));
		u.setConfirmacaoDeSenha(request.getParameter("confirmacao"));
		this.usuario = u;
	}
	
	private void salvarUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			capturarParametros(request);
			validarUsuario(this.usuario);
			dao.salvar(this.usuario);

			request.getSession().setAttribute("usuario", this.usuario);
			request.setAttribute("sucesso", "Usuario salvo com sucesso =)");

			encaminharRequisicao("duelo?acao=mostrar", request, response);
		} catch (UsuarioException e) {
			request.setAttribute("erro", e.getMessage());
			encaminharRequisicao("usuario.jsp", request, response);
		} catch (Exception e) {
			request.setAttribute("erro", "Problema ao salvar Usuario =(");
			System.out.println(e.getMessage());
			encaminharRequisicao("usuario.jsp", request, response);
		}
	}
	
	private void efetuarLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			capturarParametros(request);
			Usuario usuarioBanco = dao.buscarUsuario(this.usuario.getEmail());
			validarLogin(this.usuario, usuarioBanco);
			request.getSession().setAttribute("usuario", usuarioBanco);
			request.setAttribute("sucesso", "Login efetuado com sucesso!");
			encaminharRequisicao("duelo", request, response);
		} catch (UsuarioException e) {
			request.setAttribute("erro", e.getMessage());
			mostrarLogin(request, response);
		} catch (Exception e) {
			request.setAttribute("erro", "Problema ao efetuar login =(");
			mostrarLogin(request, response);
		}
	}	
	
	private void validarLogin(Usuario usuarioLogin, Usuario usuarioBanco) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (usuarioBanco == null) {
			throw new UsuarioException("Usu�rio n�o cadastrado");
		}
		
		if (!usuarioBanco.getSenha().equals(StringUtils.criptografarSenha(usuarioLogin.getSenha()))) {
			throw new UsuarioException("Usu�rio ou senha inv�lidos");
		}
	}

	private void mostrarLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		encaminharRequisicao("login.jsp", request, response);
	}
	
	private void mostrarLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		encaminharRequisicao("logout.jsp", request, response);
	}
	
	private void efetuarLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		encaminharRequisicao("login.jsp", request, response);
	}
	
	private void validarUsuario(Usuario usuario) throws UsuarioException {
		if ("".equals(usuario.getNome().trim())  ||
			"".equals(usuario.getEmail().trim()) ||
			"".equals(usuario.getSenha().trim())) {
			throw new UsuarioException("Todos os campos devem ser preenchidos.");
		}
		
		if (!usuario.getSenha().trim().equals(usuario.getConfirmacaoDeSenha().trim())) {
			throw new UsuarioException("A senha deve ser identica a confirma��o de senha.");
		}
		
		if (usuario.getSenha().trim().length() < 6) {
			throw new UsuarioException("A senha deve conter no m�nimo 6 d�gitos.");
		}
		
		String regex = "([0-9].*[a-zA-Z])|([a-zA-Z].*[0-9])";
		if (!usuario.getSenha().matches(regex)) {
			throw new UsuarioException("A senha deve conter n�meros e letras.");
		}
	}

	private void mostrarCadastro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		encaminharRequisicao("usuario.jsp", request, response);
	}
	
	private void encaminharRequisicao(String destino, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher(destino);
		rd.forward(request, response);
	}

}
















