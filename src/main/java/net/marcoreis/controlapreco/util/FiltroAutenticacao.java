package net.marcoreis.controlapreco.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class FiltroAutenticacao implements Filter {

    private static Logger logger = Logger.getLogger(FiltroAutenticacao.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Inicio das atividades");
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    public void destroy() {
        logger.info("Destroying");
    }

}
