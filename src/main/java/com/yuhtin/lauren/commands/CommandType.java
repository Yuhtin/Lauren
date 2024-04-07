package com.yuhtin.lauren.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    HELP("Ajuda", "Este módulo tem comandos para te ajudar na utilização do bot e do servidor.", "❓"),
    MUSIC("Música", "Comandos relacionados ao meu sistema de tocar batidões.", "\uD83C\uDFB6"),
    UTILITY("Utilidade", "Este módulo possui coisas úteis pro eu dia a dia.", "\uD83D\uDEE0"),
    SCRIM("Scrim", "Aqui você pode encontrar comandos relacionados ao meu sistema de partidas.", "\uD83D\uDC7E"),
    OTHER("Outros", "Aqui você pode encontrar comandos sem categoria.", "\uD83D\uDC7E"),

    CONFIG("Configurações", "Em configurações você define preferências de como agirei em seu servidor.", "⚙"),
    CUSTOM_MESSAGES("Mensagens Customizadas", "Este módulo possui algumas de minhas mensagens customizadas.", "\uD83D\uDD79"),
    ADMIN("Admin", "Comandos para dar suporte aos moderadores do servidor.", "\uD83E\uDDF0");

    private final String name;
    private final String description;
    private final String emoji;
}