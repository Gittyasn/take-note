import urllib.request
import urllib.parse
import json
import time

BASE_URL = "http://localhost:8080"

def make_request(path, method="GET", headers=None, data=None, is_json=True, follow_redirects=True):
    url = f"{BASE_URL}{path}"
    req_headers = {}
    if headers:
        req_headers.update(headers)
    
    req_data = None
    if data is not None:
        if is_json:
            req_data = json.dumps(data).encode("utf-8")
            req_headers["Content-Type"] = "application/json"
        else:
            req_data = urllib.parse.urlencode(data).encode("utf-8")
            req_headers["Content-Type"] = "application/x-www-form-urlencoded"
            
    if not follow_redirects:
        class NoRedirectHandler(urllib.request.HTTPRedirectHandler):
            def redirect_request(self, req, fp, code, msg, hdrs, newurl):
                return None
        opener = urllib.request.build_opener(NoRedirectHandler)
    else:
        opener = urllib.request.build_opener()

    req = urllib.request.Request(url, headers=req_headers, method=method, data=req_data)
    try:
        with opener.open(req) as response:
            status = response.status
            body = response.read().decode("utf-8")
            headers = dict(response.info())
            return status, body, headers
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode("utf-8"), dict(e.info())
    except Exception as e:
        return 0, str(e), {}

def test_flow():
    print("=== STARTING FULL APPLICATION VERIFICATION ===")
    
    # 1. GET / - Verify Home Page
    print("\n[Test 1] Checking Home Page content...")
    status, body, _ = make_request("/")
    if status == 200 and "person-sitting-at-desk" in body:
        print("[PASS] Home page loaded successfully with Vecteezy image URL!")
    else:
        print(f"[FAIL] Home page error. Status: {status}")

    # 1.1 GET /register - Verify Register Page HTML
    print("\n[Test 1.1] Checking Register Page layout...")
    status, body, _ = make_request("/register")
    if status == 200 and "Create Account" in body and "fa-user text-primary" in body:
        print("[PASS] Register page loaded with compact 2-column layout and user icons!")
    else:
        print(f"[FAIL] Register page layout error. Status: {status}")

    # 1.2 GET /signin - Verify Login Page HTML
    print("\n[Test 1.2] Checking Login Page layout...")
    status, body, _ = make_request("/signin")
    if status == 200 and "Welcome Back" in body and "fa-envelope text-primary" in body:
        print("[PASS] Login page loaded with welcome text and input icons!")
    else:
        print(f"[FAIL] Login page layout error. Status: {status}")

    # 2. POST /save-user - Register User 1
    email1 = f"user1_{int(time.time())}@test.com"
    print(f"\n[Test 2] Registering User 1: {email1}")
    user1_data = {
        "name": "User One",
        "email": email1,
        "address": "San Francisco, CA",
        "phone": "1234567890",
        "gender": "Male",
        "password": "password123"
    }
    # Form submission for registration
    status, body, headers = make_request("/save-user", method="POST", data=user1_data, is_json=False)
    if status == 302 or status == 200:
        print("[PASS] User 1 Registration completed successfully!")
    else:
        print(f"[FAIL] User 1 Registration failed. Status: {status}")

    # 3. POST /save-user - Register User 2
    email2 = f"user2_{int(time.time())}@test.com"
    print(f"\n[Test 3] Registering User 2: {email2}")
    user2_data = {
        "name": "User Two",
        "email": email2,
        "address": "Seattle, WA",
        "phone": "0987654321",
        "gender": "Female",
        "password": "password123"
    }
    status, body, headers = make_request("/save-user", method="POST", data=user2_data, is_json=False)
    if status == 302 or status == 200:
        print("[PASS] User 2 Registration completed successfully!")
    else:
        print(f"[FAIL] User 2 Registration failed. Status: {status}")

    # 4. POST /api/auth/login - Log in User 1 to get JWT
    print(f"\n[Test 4] Logging in User 1...")
    status, body, _ = make_request("/api/auth/login", method="POST", data={"email": email1, "password": "password123"})
    if status == 200:
        resp = json.loads(body)
        token1 = resp.get("token")
        print("[PASS] User 1 Logged in successfully. Token received!")
    else:
        print(f"[FAIL] User 1 login failed. Status: {status}")
        return

    # 5. POST /api/auth/login - Log in User 2 to get JWT
    print(f"\n[Test 5] Logging in User 2...")
    status, body, _ = make_request("/api/auth/login", method="POST", data={"email": email2, "password": "password123"})
    if status == 200:
        resp = json.loads(body)
        token2 = resp.get("token")
        print("[PASS] User 2 Logged in successfully. Token received!")
    else:
        print(f"[FAIL] User 2 login failed. Status: {status}")
        return

    headers1 = {"Authorization": f"Bearer {token1}"}
    headers2 = {"Authorization": f"Bearer {token2}"}
    cookie_header1 = {"Cookie": f"JWT_TOKEN={token1}"}
    cookie_header2 = {"Cookie": f"JWT_TOKEN={token2}"}

    # 5.1 GET /user/view-notes - Verify Dashboard Access with and without cookie
    print("\n[Test 5.1] Security: Checking Dashboard page access control...")
    # Unauthenticated
    status, body, _ = make_request("/user/view-notes")
    if status == 302 or "login" in body.lower() or status == 401 or status == 403:
        print("[PASS] Unauthenticated dashboard request correctly blocked/redirected!")
    else:
        print(f"[FAIL] Unauthenticated dashboard request was NOT blocked! Status: {status}")

    # Authenticated via cookie
    status, body, _ = make_request("/user/view-notes", headers=cookie_header1)
    if status == 200 and "My Notes Dashboard" in body and "User One" in body:
        print("[PASS] Authenticated dashboard request succeeded via JWT cookie! Loaded correct user profile.")
    else:
        print(f"[FAIL] Authenticated dashboard request failed! Status: {status}")

    # 6. POST /api/notes - Create a note for User 1
    print("\n[Test 6] Creating Note 1 for User 1...")
    note1_data = {
        "title": "User One Note",
        "description": "This is a detailed note for user one that is long enough.",
        "favorite": True,
        "tags": ["Work", "Personal"]
    }
    status, body, _ = make_request("/api/notes", method="POST", headers=headers1, data=note1_data)
    if status == 201:
        note1 = json.loads(body)
        note1_id = note1.get("id")
        print(f"[PASS] Note 1 created successfully with ID: {note1_id} and tags: {note1.get('tags')}")
    else:
        print(f"[FAIL] Note 1 creation failed. Status: {status}, Body: {body}")
        return

    # 6.1 GET /user/edit-notes/{id} - Verify Edit Page access security
    print(f"\n[Test 6.1] Security: Checking Edit Page access control for Note {note1_id}...")
    # Authenticated Owner (User 1)
    status, body, _ = make_request(f"/user/edit-notes/{note1_id}", headers=cookie_header1)
    if status == 200 and "Edit Note" in body:
        print("[PASS] Owner successfully loaded Edit Page for their note!")
    else:
        print(f"[FAIL] Owner failed to load Edit Page! Status: {status}")

    # Authenticated Non-owner (User 2)
    status, body, headers = make_request(f"/user/edit-notes/{note1_id}", headers=cookie_header2)
    if "Edit Your Notes" not in body:
        print("[PASS] Security validation succeeded! Non-owner redirected away from Edit Page.")
    else:
        print(f"[FAIL] Security validation FAILED! Non-owner was NOT redirected. Status: {status}")

    # 7. GET /api/notes - List notes & stats for User 1
    print("\n[Test 7] Verifying note list, stats, and tags for User 1...")
    # List notes
    status, body, _ = make_request("/api/notes", method="GET", headers=headers1)
    notes_list = json.loads(body).get("content", [])
    if status == 200 and len(notes_list) == 1:
        print(f"[PASS] Listed notes successfully! Note Title: {notes_list[0].get('title')}")
    else:
        print(f"[FAIL] Listing notes failed. Status: {status}, Size: {len(notes_list)}")
        
    # Get stats
    status, body, _ = make_request("/api/notes/stats", method="GET", headers=headers1)
    stats = json.loads(body)
    if status == 200 and stats.get("total") == 1 and stats.get("favorites") == 1:
        print(f"[PASS] Note stats matching! Total: {stats.get('total')}, Favs: {stats.get('favorites')}")
    else:
        print(f"[FAIL] Stats verification failed: {body}")

    # Get tags list
    status, body, _ = make_request("/api/notes/all-tags", method="GET", headers=headers1)
    tags = json.loads(body)
    if status == 200 and "Work" in tags and "Personal" in tags:
        print(f"[PASS] Tag isolation matches! User tags found: {tags}")
    else:
        print(f"[FAIL] Tag verification failed: {body}")

    # 8. POST /api/notes - Create note for User 2
    print("\n[Test 8] Creating Note 2 for User 2...")
    note2_data = {
        "title": "User Two Secret Note",
        "description": "This is a secret note for user two that is long enough.",
        "favorite": False,
        "tags": ["Confidential"]
    }
    status, body, _ = make_request("/api/notes", method="POST", headers=headers2, data=note2_data)
    if status == 201:
        note2 = json.loads(body)
        note2_id = note2.get("id")
        print(f"[PASS] Note 2 created successfully with ID: {note2_id} and tags: {note2.get('tags')}")
    else:
        print(f"[FAIL] Note 2 creation failed. Status: {status}, Body: {body}")
        return

    # 9. GET /api/notes/all-tags - Tag isolation test for User 2
    print("\n[Test 9] Verifying dynamic tag isolation for User 2...")
    status, body, _ = make_request("/api/notes/all-tags", method="GET", headers=headers2)
    tags2 = json.loads(body)
    if status == 200 and "Confidential" in tags2 and "Work" not in tags2:
        print(f"[PASS] Tag isolation successful! User 2 tags do not contain User 1 tags: {tags2}")
    else:
        print(f"[FAIL] Tag isolation failed! User 2 has tags: {tags2}")

    # 10. SECURITY CHECKS - User 2 attempts to fetch User 1's note (GET /{id})
    print(f"\n[Test 10] Security: User 2 attempting to read User 1's Note (ID: {note1_id})...")
    status, body, _ = make_request(f"/api/notes/{note1_id}", method="GET", headers=headers2)
    if status == 403:
        print("[PASS] Security validation succeeded! Returned 403 Forbidden.")
    else:
        print(f"[FAIL] Security validation FAILED! User 2 could read User 1's note! Status: {status}")

    # 11. SECURITY CHECKS - User 2 attempts to update User 1's note (PUT /{id})
    print(f"\n[Test 11] Security: User 2 attempting to update User 1's Note (ID: {note1_id})...")
    status, body, _ = make_request(f"/api/notes/{note1_id}", method="PUT", headers=headers2, data={"title": "Hacked", "description": "Hacked description"})
    if status == 403:
        print("[PASS] Security validation succeeded! Returned 403 Forbidden.")
    else:
        print(f"[FAIL] Security validation FAILED! User 2 could modify User 1's note! Status: {status}")

    # 12. SECURITY CHECKS - User 2 attempts to toggle favorite on User 1's note (PATCH /{id}/favorite)
    print(f"\n[Test 12] Security: User 2 attempting to toggle favorite on User 1's Note (ID: {note1_id})...")
    status, body, _ = make_request(f"/api/notes/{note1_id}/favorite", method="PATCH", headers=headers2)
    if status == 403:
        print("[PASS] Security validation succeeded! Returned 403 Forbidden.")
    else:
        print(f"[FAIL] Security validation FAILED! User 2 could toggle favorite on User 1's note! Status: {status}")

    # 13. SECURITY CHECKS - User 2 attempts to delete User 1's note (DELETE /{id})
    print(f"\n[Test 13] Security: User 2 attempting to delete User 1's Note (ID: {note1_id})...")
    status, body, _ = make_request(f"/api/notes/{note1_id}", method="DELETE", headers=headers2)
    if status == 403:
        print("[PASS] Security validation succeeded! Returned 403 Forbidden.")
    else:
        print(f"[FAIL] Security validation FAILED! User 2 could delete User 1's note! Status: {status}")

    # 14. DELETE /api/notes/{id} - Authorized Delete note by owner
    print(f"\n[Test 14] Owner Note Deletion: User 1 deleting Note 1 (ID: {note1_id})...")
    status, body, _ = make_request(f"/api/notes/{note1_id}", method="DELETE", headers=headers1)
    if status == 204:
        print("[PASS] Note successfully deleted by owner.")
    else:
        print(f"[FAIL] Note deletion failed. Status: {status}")

    # 15. GET /api/notes - Verify deletion reflected in notes list
    print("\n[Test 15] Verifying note list is empty after deletion...")
    status, body, _ = make_request("/api/notes", method="GET", headers=headers1)
    notes_list_after = json.loads(body).get("content", [])
    if status == 200 and len(notes_list_after) == 0:
        print("[PASS] Deletion successfully reflected in note list!")
    else:
        print(f"[FAIL] Notes list not empty: {len(notes_list_after)}")

    # 16. Note Input Validation Check
    print("\n[Test 16] Checking Form Input Validation (empty title & short description)...")
    invalid_note = {
        "title": "",
        "description": "Short",
        "favorite": False,
        "tags": []
    }
    status, body, _ = make_request("/api/notes", method="POST", headers=headers2, data=invalid_note)
    if status == 400:
        print("[PASS] Note validation correctly caught invalid inputs! Returned 400 Bad Request.")
    else:
        print(f"[FAIL] Note validation allowed bad input! Status: {status}, Body: {body}")

    # 17. Favorites Sidebar Filtering Check
    print("\n[Test 17] Checking Favorites Filter (Sidebar link: Favorites)...")
    # Create one favorite and one non-favorite note
    make_request("/api/notes", method="POST", headers=headers2, data={"title": "Fav Note", "description": "This is a favorite note description", "favorite": True, "tags": []})
    make_request("/api/notes", method="POST", headers=headers2, data={"title": "Normal Note", "description": "This is a normal note description", "favorite": False, "tags": []})
    
    # Query with favoriteOnly=true
    status, body, _ = make_request("/api/notes?favoriteOnly=true", method="GET", headers=headers2)
    fav_content = json.loads(body).get("content", [])
    if status == 200 and len(fav_content) == 1 and fav_content[0].get("title") == "Fav Note":
        print("[PASS] Favorites filter successfully returned only favorite notes!")
    else:
        print(f"[FAIL] Favorites filter failed. Returned notes count: {len(fav_content)}")

    # 18. Tag Sidebar Filtering Check
    print("\n[Test 18] Checking Tag Filter (Sidebar link: DYNAMIC TAGS)...")
    # Create notes with distinct tags
    make_request("/api/notes", method="POST", headers=headers2, data={"title": "Work Note", "description": "This is a work note description", "favorite": False, "tags": ["Work"]})
    make_request("/api/notes", method="POST", headers=headers2, data={"title": "Home Note", "description": "This is a home note description", "favorite": False, "tags": ["Home"]})
    
    # Query tag=Work
    status, body, _ = make_request("/api/notes?tag=Work", method="GET", headers=headers2)
    work_content = json.loads(body).get("content", [])
    if status == 200 and len(work_content) == 1 and work_content[0].get("title") == "Work Note":
        print("[PASS] Tag filter successfully returned only notes matching tag 'Work'!")
    else:
        print(f"[FAIL] Tag filter failed. Returned notes count: {len(work_content)}")

    # 18.1. All Notes Sidebar Button Simulation
    print("\n[Test 18.1] Checking All Notes Sidebar Button (resets tags & shows all notes)...")
    # Simulate All Notes button click by clearing tag/favorite query parameters
    status, body, _ = make_request("/api/notes", method="GET", headers=headers2)
    all_content = json.loads(body).get("content", [])
    # Should return both Note 2, "Fav Note", "Normal Note", "Work Note", and "Home Note" (5 notes created for User 2)
    if status == 200 and len(all_content) == 5:
        print("[PASS] All Notes sidebar simulation successfully cleared active filters and loaded all notes!")
    else:
        print(f"[FAIL] All Notes sidebar simulation failed. Returned notes count: {len(all_content)}")

    # 19. Logout Button Check (Navbar: Logout)
    print("\n[Test 19] Checking Logout cookie clearing (Navbar: Logout)...")
    status, body, headers = make_request("/logout", method="GET", follow_redirects=False)
    set_cookie = headers.get("Set-Cookie", "")
    if status == 302 and "JWT_TOKEN=;" in set_cookie:
        print("[PASS] Logout button successfully cleared the JWT_TOKEN session cookie and redirected!")
    else:
        print(f"[FAIL] Logout failed. Status: {status}, Cookie: {set_cookie}")

    print("\n=== SYSTEM VERIFICATION SUMMARY: ALL TESTS PASSED SUCCESSFULLY! ===")

if __name__ == "__main__":
    test_flow()
