package br.com.petfamily.canilapi.mapper;

import br.com.petfamily.canilapi.controller.dto.NinhadaDetalhesDTO;
import br.com.petfamily.canilapi.model.Ninhada;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface do Mapper para conversões relacionadas à entidade Ninhada.
 * A anotação @Mapper com componentModel="spring" permite que o Spring
 * injete esta interface como um bean.
 */
@Mapper(componentModel = "spring")
public interface NinhadaMapper {

    /**
     * Converte uma entidade Ninhada para o seu DTO de detalhes.
     * O MapStruct é inteligente o suficiente para mapear as listas e objetos internos.
     * @param ninhada A entidade de origem.
     * @return O DTO de detalhes preenchido.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "dataNascimento", source = "dataNascimento")
    @Mapping(target = "mae", source = "mae")
    @Mapping(target = "pai", source = "pai")
    @Mapping(target = "filhotes", source = "filhotes")
    NinhadaDetalhesDTO toDetalhesDTO(Ninhada ninhada);
}