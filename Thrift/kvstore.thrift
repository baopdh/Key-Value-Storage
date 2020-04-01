namespace java kvstore

typedef i32 int

struct User {
	1: string userName,
	2: string email,
	3: string phone,
}

service KVStoreService {
	void ping(),
	User get(1: int id),
	bool remove(1: int id),
	int put(1: User user),
}
