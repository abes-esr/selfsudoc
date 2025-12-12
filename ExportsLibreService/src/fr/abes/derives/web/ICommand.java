package fr.abes.derives.web;

public interface ICommand {
    public String execute(RequestHelper helper) throws javax.servlet.ServletException, java.io.IOException;
}
