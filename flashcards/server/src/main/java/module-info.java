module flashcards.server {
    // Required Java modules
    requires java.base;
    requires java.logging;
    requires java.net.http;
    
    // Jackson for JSON processing (these are proper modules)
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    
    // Internal dependencies
    requires transitive flashcards.storage;
    requires transitive flashcards.shared;
    requires transitive flashcards.core;

    requires spring.context;
    requires spring.web;
    requires spring.beans;
    requires spring.core;

    // Export packages
    exports server;
    exports server.controller;
    exports server.service;
    
    // Open packages for reflection (Spring will access from unnamed modules)
    opens server;
    opens server.controller;
    opens server.service;
}
