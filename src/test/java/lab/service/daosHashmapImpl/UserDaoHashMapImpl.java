package lab.service.daosHashmapImpl;

import lab.daos.UsersDAO;
import lab.db.tables.pojos.Users;
import org.jooq.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDaoHashMapImpl extends UsersDAO {
	private HashMap<Integer, Users> usersHashMap = new HashMap<>();
	
	@Override
	public Users store(Users user) {
		int i = 1;
		while (usersHashMap.get(i) != null) {
			i++;
		}
		usersHashMap.put(i, user.setId(i));
		return user;
	}
	
	@Override
	public Users getUserByName(String username) {
		return usersHashMap.values().stream().filter(user -> user.getUserName().equals(username)).findFirst().get();
	}
	
	@Override
	public Users fetchOneById(Integer value) {
		return usersHashMap.get(value);
	}
	
	@Override
	public <Z> Optional<Users> fetchOptional(Field<Z> field, Z value) {
		if (field.equals("USERS.USER_NAME")) {
			return usersHashMap.values().stream().filter(user -> user.getUserName().equals(value)).findAny();
		}
		
		return null;
	}
	
	public void deleteAll() {
		usersHashMap = new HashMap<>();
	}
	
	public List<Users> findAll() {
		return usersHashMap.values().stream().collect(Collectors.toList());
	}
}
