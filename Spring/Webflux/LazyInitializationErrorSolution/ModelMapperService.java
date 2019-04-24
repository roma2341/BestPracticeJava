import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

import com.shs.crm.dao.entity.user.CrmUser;
import com.shs.crm.dto.account.user.CrmUserDto;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ModelMapperService {

	ModelMapper mapper;

	@PersistenceContext()
	EntityManager entityManager;

	@PostConstruct
	public void init(){
		mapper = new ModelMapper();
		mapper.getConfiguration().addValueReader(new CustomValueReader());
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		mapper.getConfiguration().setSourceNameTokenizer(new CustomMapperTokenizer());//default mapper tokenizer is slow, my version is faster but less functional
		mapper.getConfiguration().setDestinationNameTokenizer(new CustomMapperTokenizer());
		mapper.getConfiguration().setPropertyCondition(new Condition<Object, Object>() {
			public boolean applies(MappingContext<Object, Object> context) {
				return Hibernate.isInitialized(context.getSource());
			}
		});
		this.addRules();
	}
	
	private void addRules(){
		var typeMap = mapper.createTypeMap(CrmUser.class, CrmUserDto.class).addMapping(CrmUser::hasActiveGoogle, CrmUserDto::setHasActiveGoogle);
	}

	public <D, T> D map(final T entity, Class<D> outClass) {
		return mapper.map(entity, outClass);
	}

	public  <D, T> List<D> mapAll(final Iterable<T> entityList, Class<D> outClass) {
		List<D> result = new ArrayList();
		for(var item : entityList){
			result.add(map(item,outClass));
		}
		return result;
	}

	public  <S, D> D map(final S source, D destination) {
		mapper.map(source, destination);
		return destination;
	}
			
}
