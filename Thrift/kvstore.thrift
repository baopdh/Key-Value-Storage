namespace java kvstore

typedef i32 int

struct User {
	1: string userName,
	2: string email,
	3: string phone,
}

enum TASK {
	PUT, DELETE, WARNING
}

struct Task {
	1: int id,
	2: User user,
	3: TASK task
}

service KVStoreService {
	void ping(),
	User get(1: int id),
	bool remove(1: int id),
	bool put(1: int id, 2: User user),
	int getKey(),
}
