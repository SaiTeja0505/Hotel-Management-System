import { useMemo, useState } from 'react'
import './App.css'

const users = [
  { id: 1, name: 'Admin User', email: 'admin@example.com', password: 'admin', role: 'ADMIN', status: 'ACTIVE' },
  { id: 2, name: 'Owner User', email: 'owner@example.com', password: 'owner', role: 'HOTEL_OWNER', status: 'APPROVED' },
  { id: 3, name: 'Normal User', email: 'user@example.com', password: 'user', role: 'USER', status: 'ACTIVE' },
]

const hotels = [
  {
    id: 1,
    name: 'Lunar View Suites',
    description: 'Upscale comfort with city skyline views.',
    city: 'New York',
    rating: 4.9,
    status: 'APPROVED',
    address: '578 Madison Ave, NY',
    images: [
      'https://images.unsplash.com/photo-1501117716987-c8e5dbf4eef8?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1519821172141-bb2f0bb26b44?auto=format&fit=crop&w=1200&q=80',
    ],
    amenities: ['WiFi', 'AC', 'Breakfast', 'Parking'],
  },
  {
    id: 2,
    name: 'Aurora Grand Hotel',
    description: 'Stylish rooms near downtown attractions.',
    city: 'San Francisco',
    rating: 4.6,
    status: 'APPROVED',
    address: '120 Market St, SF',
    images: [
      'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80',
    ],
    amenities: ['WiFi', 'AC', 'Swimming Pool', 'Breakfast'],
  },
  {
    id: 3,
    name: 'Ocean Glow Inn',
    description: 'Relaxing seaside stay with modern touches.',
    city: 'Miami',
    rating: 4.7,
    status: 'APPROVED',
    address: '14 Ocean Dr, Miami',
    images: [
      'https://images.unsplash.com/photo-1501117716987-c8e5dbf4eef8?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80',
    ],
    amenities: ['WiFi', 'AC', 'Parking', 'Breakfast'],
  },
]

const rooms = [
  {
    id: 101,
    hotelId: 1,
    room_number: '310',
    room_type: 'Deluxe Suite',
    price_per_night: 185,
    capacity: 2,
    max_guests: 3,
    description: 'Room with skyline views, king bed, and workspace.',
    total_rooms: 5,
    images: ['https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=80'],
    amenities: ['WiFi', 'AC', 'Breakfast'],
  },
  {
    id: 102,
    hotelId: 2,
    room_number: '423',
    room_type: 'Executive Room',
    price_per_night: 145,
    capacity: 2,
    max_guests: 2,
    description: 'Comfortable room with king-size bed and quiet environment.',
    total_rooms: 3,
    images: ['https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1200&q=80'],
    amenities: ['WiFi', 'AC', 'Parking'],
  },
  {
    id: 103,
    hotelId: 3,
    room_number: '101',
    room_type: 'Beachside King',
    price_per_night: 170,
    capacity: 2,
    max_guests: 4,
    description: 'Close to the beach with queen bunk option.',
    total_rooms: 4,
    images: ['https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1200&q=80'],
    amenities: ['WiFi', 'AC', 'Swimming Pool'],
  },
]

const initialBookings = []

const formatDate = (iso) => {
  if (!iso) return ''
  return new Date(iso).toLocaleDateString()
}

const dateDiffDays = (from, to) => {
  if (!from || !to) return 0
  const diff = new Date(to) - new Date(from)
  return diff > 0 ? Math.round(diff / (1000 * 60 * 60 * 24)) : 0
}

function App() {
  const [authUser, setAuthUser] = useState(null)
  const [authForm, setAuthForm] = useState({ email: '', password: '', name: '', role: 'USER' })
  const [page, setPage] = useState({ name: 'login' })
  const [filters, setFilters] = useState({
    city: 'All',
    minPrice: 0,
    maxPrice: 1000,
    guests: 1,
    checkIn: '',
    checkOut: '',
    search: '',
  })
  const [bookings, setBookings] = useState(initialBookings)
  const [selectedRooms, setSelectedRooms] = useState([])
  const [bookingForm, setBookingForm] = useState({
    checkIn: '',
    checkOut: '',
    guests: 1,
  })
  const [hotelsData, setHotelsData] = useState(hotels)
  const [roomsData, setRoomsData] = useState(rooms)
  const [ownerApprovals, setOwnerApprovals] = useState([
    { id: 1, owner_id: 2, approved_by: null, status: 'PENDING', remarks: 'Owner signup', created_at: new Date().toISOString() },
  ])
  const [hotelForm, setHotelForm] = useState({ id: null, name: '', description: '', city: '', address: '', amenities: '' })
  const [roomForm, setRoomForm] = useState({ id: null, hotelId: '', room_number: '', room_type: '', price_per_night: '', capacity: '', max_guests: '', description: '', total_rooms: '', amenities: '' })
  const [showRoleSection, setShowRoleSection] = useState('')

  const currentHotel = hotelsData.find((h) => h.id === page.hotelId)
  const currentRoom = roomsData.find((r) => r.id === page.roomId)

  const filteredHotels = useMemo(() => {
    return hotelsData.filter((hotel) => {
      if (hotel.status !== 'APPROVED') return false
      if (filters.city !== 'All' && hotel.city !== filters.city) return false
      if (filters.search && !hotel.name.toLowerCase().includes(filters.search.toLowerCase()) && !hotel.description.toLowerCase().includes(filters.search.toLowerCase()))
        return false
      const maxRoom = roomsData.filter((r) => r.hotelId === hotel.id).reduce((acc, room) => Math.max(acc, room.price_per_night), 0)
      if (maxRoom > filters.maxPrice || maxRoom < filters.minPrice) return false
      return true
    })
  }, [filters, hotelsData, roomsData])

  const handleLogin = () => {
    const found = users.find((u) => u.email === authForm.email && u.password === authForm.password)
    if (!found) {
      alert('Invalid email or password')
      return
    }
    setAuthUser(found)
    setPage({ name: 'home' })
  }

  const handleRegister = () => {
    if (!authForm.name || !authForm.email || !authForm.password) {
      alert('Fill out all fields')
      return
    }
    if (users.some((u) => u.email === authForm.email)) {
      alert('Email already exists')
      return
    }
    const newUser = {
      id: Date.now(),
      name: authForm.name,
      email: authForm.email,
      password: authForm.password,
      role: 'USER',
      status: 'ACTIVE',
    }
    users.push(newUser)
    setAuthUser(newUser)
    setPage({ name: 'home' })
  }

  const handleLogout = () => {
    setAuthUser(null)
    setPage({ name: 'login' })
    setAuthForm({ email: '', password: '', name: '', role: 'USER' })
  }

  const handleApproveOwner = (approvalId, approve) => {
    setOwnerApprovals((prev) =>
      prev.map((item) =>
        item.id === approvalId
          ? {
              ...item,
              status: approve ? 'APPROVED' : 'REJECTED',
              approved_by: authUser?.id ?? null,
              updated_at: new Date().toISOString(),
            }
          : item,
      ),
    )
  }

  const handleToggleHotelStatus = (hotelId, newStatus) => {
    setHotelsData((prev) => prev.map((hotel) => (hotel.id === hotelId ? { ...hotel, status: newStatus } : hotel)))
  }

  const handleAddHotel = () => {
    if (!hotelForm.name || !hotelForm.city || !hotelForm.address) {
      alert('Hotel name, city, and address are required')
      return
    }
    const newHotel = {
      id: Date.now(),
      name: hotelForm.name,
      description: hotelForm.description,
      city: hotelForm.city,
      address: hotelForm.address,
      rating: 0,
      status: 'PENDING',
      images: [],
      amenities: hotelForm.amenities ? hotelForm.amenities.split(',').map((a) => a.trim()) : [],
    }
    setHotelsData((prev) => [newHotel, ...prev])
    setHotelForm({ id: null, name: '', description: '', city: '', address: '', amenities: '' })
    alert('Hotel submitted for approval')
  }

  const handleAddRoom = () => {
    if (!roomForm.hotelId || !roomForm.room_number || !roomForm.price_per_night) {
      alert('Hotel, room number, and price are required')
      return
    }
    const newRoom = {
      id: Date.now(),
      hotelId: Number(roomForm.hotelId),
      room_number: roomForm.room_number,
      room_type: roomForm.room_type,
      price_per_night: Number(roomForm.price_per_night),
      capacity: Number(roomForm.capacity),
      max_guests: Number(roomForm.max_guests),
      description: roomForm.description,
      total_rooms: Number(roomForm.total_rooms),
      images: [],
      amenities: roomForm.amenities ? roomForm.amenities.split(',').map((a) => a.trim()) : [],
    }
    setRoomsData((prev) => [newRoom, ...prev])
    setRoomForm({ id: null, hotelId: '', room_number: '', room_type: '', price_per_night: '', capacity: '', max_guests: '', description: '', total_rooms: '', amenities: '' })
    alert('Room added')
  }

  const handleDeleteRoom = (roomId) => {
    setRoomsData((prev) => prev.filter((room) => room.id !== roomId))
  }

  const handleUpdateHotel = (hotel) => {
    setHotelForm({ ...hotel, amenities: hotel.amenities.join(', ') })
    setPage({ name: 'hotel-owner-manage-hotels' })
  }

  const handleSaveHotel = () => {
    if (!hotelForm.id) {
      handleAddHotel()
      return
    }
    setHotelsData((prev) => prev.map((hotel) => (hotel.id === hotelForm.id ? { ...hotel, ...hotelForm, amenities: hotelForm.amenities.split(',').map((a) => a.trim()) } : hotel)))
    setHotelForm({ id: null, name: '', description: '', city: '', address: '', amenities: '' })
    alert('Hotel updated')
  }

  const handleDeleteHotel = (hotelId) => {
    setHotelsData((prev) => prev.filter((hotel) => hotel.id !== hotelId))
    setRoomsData((prev) => prev.filter((room) => room.hotelId !== hotelId))
  }

  const handleNewBooking = () => {
    if (!currentRoom) return
    if (!bookingForm.checkIn || !bookingForm.checkOut) return
    const nights = dateDiffDays(bookingForm.checkIn, bookingForm.checkOut)
    if (nights <= 0) return

    const newBooking = {
      id: Date.now(),
      booking_reference: `REF-${Date.now()}`,
      user_id: 1,
      hotel_id: currentRoom.hotelId,
      room_id: currentRoom.id,
      check_in_date: bookingForm.checkIn,
      check_out_date: bookingForm.checkOut,
      guests: bookingForm.guests,
      total_price: currentRoom.price_per_night * nights,
      status: 'CONFIRMED',
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString(),
    }

    setBookings((prev) => [newBooking, ...prev])
    setSelectedRooms((prev) => [...prev, currentRoom.id])
    setPage({ name: 'my-bookings' })
  }

  const handleCancelBooking = (id) => {
    setBookings((prev) =>
      prev.map((booking) =>
        booking.id === id ? { ...booking, status: 'CANCELLED', updated_at: new Date().toISOString() } : booking,
      ),
    )
  }

  const pageContent = () => {
    if (page.name === 'home') {
      const cities = ['All', ...new Set(hotelsData.map((h) => h.city))]
      return (
        <>
          <h1>Hotel Search</h1>
          <div className="filters">
            <input
              type="text"
              value={filters.search}
              placeholder="Search hotels..."
              onChange={(e) => setFilters((f) => ({ ...f, search: e.target.value }))}
            />
            <select value={filters.city} onChange={(e) => setFilters((f) => ({ ...f, city: e.target.value }))}>
              {cities.map((city) => (
                <option value={city} key={city}>{city}</option>
              ))}
            </select>
            <input
              type="number"
              value={filters.minPrice}
              min={0}
              max={1000}
              onChange={(e) => setFilters((f) => ({ ...f, minPrice: Number(e.target.value) }))}
              placeholder="Min price"
            />
            <input
              type="number"
              value={filters.maxPrice}
              min={0}
              max={10000}
              onChange={(e) => setFilters((f) => ({ ...f, maxPrice: Number(e.target.value) }))}
              placeholder="Max price"
            />
            <input
              type="number"
              value={filters.guests}
              min={1}
              max={10}
              onChange={(e) => setFilters((f) => ({ ...f, guests: Number(e.target.value) }))}
              placeholder="Guests"
            />
            <input
              type="date"
              value={filters.checkIn}
              onChange={(e) => setFilters((f) => ({ ...f, checkIn: e.target.value }))}
            />
            <input
              type="date"
              value={filters.checkOut}
              onChange={(e) => setFilters((f) => ({ ...f, checkOut: e.target.value }))}
            />
          </div>
          <div className="hotel-grid">
            {filteredHotels.length ? (
              filteredHotels.map((hotel) => (
                <article className="hotel-card" key={hotel.id}>
                  <img src={hotel.images[0]} alt={hotel.name} />
                  <div className="hotel-body">
                    <h2>{hotel.name}</h2>
                    <p>{hotel.description}</p>
                    <p className="meta">{hotel.city} · {hotel.address}</p>
                    <p className="meta">Rating: {hotel.rating}</p>
                    <button onClick={() => setPage({ name: 'hotel-details', hotelId: hotel.id })}>
                      View Details
                    </button>
                  </div>
                </article>
              ))
            ) : (
              <p className="empty-state">No hotels match your filters.</p>
            )}
          </div>
        </>
      )
    }

    if (page.name === 'hotel-details' && currentHotel) {
      const hotelRooms = roomsData.filter((r) => r.hotelId === currentHotel.id)
      return (
        <>
          <h1>{currentHotel.name}</h1>
          <p className="subheading">{currentHotel.city} · {currentHotel.address}</p>
          <div className="hotel-images">
            {currentHotel.images.map((img, idx) => (
              <img key={idx} src={img} alt={`${currentHotel.name} ${idx + 1}`} />
            ))}
          </div>
          <p>{currentHotel.description}</p>
          <div className="amenities">
            <h3>Amenities</h3>
            <ul>{currentHotel.amenities.map((a) => <li key={a}>{a}</li>)}</ul>
          </div>
          <div className="room-list">
            <h3>Available Rooms</h3>
            {hotelRooms.length === 0 && <p>No room data available.</p>}
            {hotelRooms.map((room) => (
              <div className="room-card" key={room.id}>
                <img src={room.images[0] ?? currentHotel.images[0]} alt={room.room_type} />
                <div>
                  <h4>{room.room_type} - ${room.price_per_night}/night</h4>
                  <p>{room.description}</p>
                  <p>Capacity: {room.capacity}, Max guests: {room.max_guests}</p>
                  <button onClick={() => {
                    setPage({ name: 'booking', roomId: room.id })
                    setBookingForm((f) => ({ ...f, guests: Math.min(room.max_guests, f.guests) }))
                  }}>
                    Book Room
                  </button>
                </div>
              </div>
            ))}
          </div>
        </>
      )
    }

    if (page.name === 'booking' && currentRoom) {
      const hotel = hotelsData.find((h) => h.id === currentRoom.hotelId)
      const nights = dateDiffDays(bookingForm.checkIn, bookingForm.checkOut)
      const totalPrice = nights * currentRoom.price_per_night

      return (
        <>
          <h1>Booking: {currentRoom.room_type}</h1>
          <p className="subheading">{hotel?.name}</p>
          <div className="booking-pane">
            <div className="booking-left">
              <p>{currentRoom.description}</p>
              <p>Price per night: ${currentRoom.price_per_night}</p>
              <p>Capacity: {currentRoom.capacity} (max {currentRoom.max_guests})</p>

              <label>Check-in</label>
              <input
                type="date"
                value={bookingForm.checkIn}
                onChange={(e) => setBookingForm((f) => ({ ...f, checkIn: e.target.value }))}
              />
              <label>Check-out</label>
              <input
                type="date"
                value={bookingForm.checkOut}
                onChange={(e) => setBookingForm((f) => ({ ...f, checkOut: e.target.value }))}
              />
              <label>Guests</label>
              <input
                type="number"
                value={bookingForm.guests}
                min={1}
                max={currentRoom.max_guests}
                onChange={(e) => setBookingForm((f) => ({ ...f, guests: Number(e.target.value) }))}
              />
            </div>
            <div className="booking-right">
              <p>Nights: {nights}</p>
              <p>Total price: ${nights > 0 ? totalPrice : 0}</p>
              <button
                disabled={nights <= 0 || !bookingForm.checkIn || !bookingForm.checkOut}
                onClick={handleNewBooking}
              >
                Confirm Booking
              </button>
            </div>
          </div>
        </>
      )
    }

    if (page.name === 'admin-dashboard') {
      return (
        <>
          <h1>Admin Dashboard</h1>
          <p>Total users: {users.length}</p>
          <p>Total hotels: {hotelsData.length}</p>
          <p>Total rooms: {roomsData.length}</p>
          <p>Total bookings: {bookings.length}</p>
          <p>Pending owner approvals: {ownerApprovals.filter((it) => it.status === 'PENDING').length}</p>
        </>
      )
    }

    if (page.name === 'admin-users') {
      return (
        <>
          <h1>Manage Users</h1>
          <div className="hotel-grid">
            {users.map((u) => (
              <article className="hotel-card" key={u.id}>
                <div className="hotel-body">
                  <h2>{u.name}</h2>
                  <p>{u.email}</p>
                  <p>Role: {u.role}</p>
                  <p>Status: {u.status}</p>
                </div>
              </article>
            ))}
          </div>
        </>
      )
    }

    if (page.name === 'admin-hotels') {
      return (
        <>
          <h1>Hotel Approval</h1>
          <div className="hotel-grid">
            {hotelsData.map((hotel) => (
              <article className="hotel-card" key={hotel.id}>
                <img src={hotel.images[0] ?? ''} alt={hotel.name} />
                <div className="hotel-body">
                  <h2>{hotel.name}</h2>
                  <p>{hotel.city}</p>
                  <p>Status: {hotel.status}</p>
                  {hotel.status !== 'APPROVED' && (
                    <button onClick={() => handleToggleHotelStatus(hotel.id, 'APPROVED')}>Approve</button>
                  )}
                  {hotel.status !== 'REJECTED' && (
                    <button onClick={() => handleToggleHotelStatus(hotel.id, 'REJECTED')}>Reject</button>
                  )}
                </div>
              </article>
            ))}
          </div>
          <h3>Owner Approvals</h3>
          <div className="booking-history">
            {ownerApprovals.map((approval) => {
              const owner = users.find((u) => u.id === approval.owner_id)
              return (
                <div className="booking-item" key={approval.id}>
                  <div>
                    <strong>{owner?.name}</strong> - {approval.status}
                    <p>{approval.remarks}</p>
                  </div>
                  <div className="booking-actions">
                    {approval.status === 'PENDING' && (
                      <>
                        <button onClick={() => handleApproveOwner(approval.id, true)}>Approve</button>
                        <button onClick={() => handleApproveOwner(approval.id, false)}>Reject</button>
                      </>
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )
    }

    if (page.name === 'admin-bookings') {
      return (
        <>
          <h1>All Bookings</h1>
          <div className="booking-history">
            {bookings.map((booking) => {
              const hotel = hotelsData.find((h) => h.id === booking.hotel_id)
              const room = roomsData.find((r) => r.id === booking.room_id)
              const user = users.find((u) => u.id === booking.user_id)
              return (
                <div className="booking-item" key={booking.id}>
                  <div>
                    <strong>{booking.booking_reference}</strong> · {user?.name} · {hotel?.name}
                    <p>{formatDate(booking.check_in_date)} → {formatDate(booking.check_out_date)}</p>
                    <p>Status: {booking.status}</p>
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )
    }

    if (page.name === 'owner-dashboard') {
      return (
        <>
          <h1>Owner Dashboard</h1>
          <p>Hotels: {hotelsData.filter((h) => h.owner_id === authUser.id).length}</p>
          <p>Rooms: {roomsData.filter((r) => hotelsData.find((h) => h.id === r.hotelId && h.owner_id === authUser.id)).length}</p>
          <p>Bookings: {bookings.filter((b) => roomsData.find((r) => r.id === b.room_id && hotelsData.find((h) => h.id === r.hotelId && h.owner_id === authUser.id))).length}</p>
        </>
      )
    }

    if (page.name === 'owner-hotels') {
      const ownedHotels = hotelsData.filter((h) => h.owner_id === authUser.id)
      return (
        <>
          <h1>Manage My Hotels</h1>
          <div className="auth-card">
            <h3>Add / Update Hotel</h3>
            <input value={hotelForm.name} placeholder="Name" onChange={(e) => setHotelForm((f) => ({ ...f, name: e.target.value }))} />
            <input value={hotelForm.city} placeholder="City" onChange={(e) => setHotelForm((f) => ({ ...f, city: e.target.value }))} />
            <input value={hotelForm.address} placeholder="Address" onChange={(e) => setHotelForm((f) => ({ ...f, address: e.target.value }))} />
            <input value={hotelForm.description} placeholder="Description" onChange={(e) => setHotelForm((f) => ({ ...f, description: e.target.value }))} />
            <input value={hotelForm.amenities} placeholder="Amenities (comma-separated)" onChange={(e) => setHotelForm((f) => ({ ...f, amenities: e.target.value }))} />
            <button onClick={hotelForm.id ? handleSaveHotel : handleAddHotel}>{hotelForm.id ? 'Save Hotel' : 'Add Hotel'}</button>
          </div>
          <div className="hotel-grid">
            {ownedHotels.map((hotel) => (
              <article className="hotel-card" key={hotel.id}>
                <img src={hotel.images[0] ?? ''} alt={hotel.name} />
                <div className="hotel-body">
                  <h2>{hotel.name}</h2>
                  <p>{hotel.city}</p>
                  <p>Status: {hotel.status}</p>
                  <button onClick={() => { setHotelForm({ ...hotel, amenities: hotel.amenities.join(', ') }); setPage({ name: 'owner-hotels' }) }}>Edit</button>
                  <button onClick={() => handleDeleteHotel(hotel.id)}>Delete</button>
                </div>
              </article>
            ))}
          </div>
        </>
      )
    }

    if (page.name === 'owner-rooms') {
      const ownerHotels = hotelsData.filter((h) => h.owner_id === authUser.id)
      const ownerHotelIds = ownerHotels.map((h) => h.id)
      const ownerRooms = roomsData.filter((r) => ownerHotelIds.includes(r.hotelId))
      return (
        <>
          <h1>Manage Rooms</h1>
          <div className="auth-card">
            <h3>Add Room</h3>
            <select value={roomForm.hotelId} onChange={(e) => setRoomForm((f) => ({ ...f, hotelId: e.target.value }))}>
              <option value="">Select hotel</option>
              {ownerHotels.map((hotel) => <option key={hotel.id} value={hotel.id}>{hotel.name}</option>)}
            </select>
            <input value={roomForm.room_number} placeholder="Room number" onChange={(e) => setRoomForm((f) => ({ ...f, room_number: e.target.value }))} />
            <input value={roomForm.room_type} placeholder="Room type" onChange={(e) => setRoomForm((f) => ({ ...f, room_type: e.target.value }))} />
            <input value={roomForm.price_per_night} placeholder="Price per night" type="number" onChange={(e) => setRoomForm((f) => ({ ...f, price_per_night: e.target.value }))} />
            <input value={roomForm.capacity} placeholder="Capacity" type="number" onChange={(e) => setRoomForm((f) => ({ ...f, capacity: e.target.value }))} />
            <input value={roomForm.max_guests} placeholder="Max guests" type="number" onChange={(e) => setRoomForm((f) => ({ ...f, max_guests: e.target.value }))} />
            <input value={roomForm.total_rooms} placeholder="Total rooms" type="number" onChange={(e) => setRoomForm((f) => ({ ...f, total_rooms: e.target.value }))} />
            <input value={roomForm.description} placeholder="Description" onChange={(e) => setRoomForm((f) => ({ ...f, description: e.target.value }))} />
            <input value={roomForm.amenities} placeholder="Amenities (comma-separated)" onChange={(e) => setRoomForm((f) => ({ ...f, amenities: e.target.value }))} />
            <button onClick={handleAddRoom}>Add Room</button>
          </div>
          <div className="room-list">
            {ownerRooms.map((room) => (
              <div className="room-card" key={room.id}>
                <img src={room.images[0] ?? ''} alt={room.room_type} />
                <div>
                  <h4>{room.room_type} - ${room.price_per_night}/night</h4>
                  <p>{room.description}</p>
                  <p>Capacity: {room.capacity}, Max: {room.max_guests}</p>
                  <button onClick={() => handleDeleteRoom(room.id)}>Delete</button>
                </div>
              </div>
            ))}
          </div>
        </>
      )
    }

    if (page.name === 'owner-bookings') {
      const ownerHotels = hotelsData.filter((h) => h.owner_id === authUser.id)
      const ownerHotelIds = ownerHotels.map((h) => h.id)
      const ownerRoomIds = roomsData.filter((r) => ownerHotelIds.includes(r.hotelId)).map((r) => r.id)
      const ownerBookings = bookings.filter((b) => ownerRoomIds.includes(b.room_id))
      return (
        <>
          <h1>Owner Bookings</h1>
          <div className="booking-history">
            {ownerBookings.map((booking) => {
              const hotel = hotelsData.find((h) => h.id === booking.hotel_id)
              const room = roomsData.find((r) => r.id === booking.room_id)
              const user = users.find((u) => u.id === booking.user_id)
              return (
                <div className="booking-item" key={booking.id}>
                  <div>
                    <strong>{booking.booking_reference}</strong> · {user?.name} · {room?.room_type}
                    <p>{formatDate(booking.check_in_date)} → {formatDate(booking.check_out_date)}</p>
                    <p>Status: {booking.status}</p>
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )
    }

    if (page.name === 'my-bookings') {
      return (
        <>
          <h1>My Bookings</h1>
          {bookings.length === 0 && <p className="empty-state">No bookings yet.</p>}
          <div className="booking-history">
            {bookings.map((booking) => {
              const hotel = hotelsData.find((h) => h.id === booking.hotel_id)
              const room = roomsData.find((r) => r.id === booking.room_id)
              return (
                <div className="booking-item" key={booking.id}>
                  <div>
                    <strong>{booking.booking_reference}</strong> · {hotel?.name} · {room?.room_type}
                    <p>{formatDate(booking.check_in_date)} → {formatDate(booking.check_out_date)}</p>
                    <p>Status: {booking.status}</p>
                  </div>
                  <div className="booking-actions">
                    {booking.status === 'CONFIRMED' && (
                      <button className="cancel" onClick={() => handleCancelBooking(booking.id)}>
                        Cancel
                      </button>
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )
    }

    if (page.name === 'profile') {
      return (
        <>
          <h1>Profile</h1>
          <p><strong>Name:</strong> {authUser?.name}</p>
          <p><strong>Email:</strong> {authUser?.email}</p>
          <p><strong>Role:</strong> {authUser?.role}</p>
          <button onClick={() => setPage({ name: 'home' })}>Go Home</button>
        </>
      )
    }

    if (page.name === 'login') {
      return (
        <div className="auth-card">
          <h1>Login</h1>
          <input type="email" placeholder="Email" value={authForm.email} onChange={(e) => setAuthForm((f) => ({ ...f, email: e.target.value }))} />
          <input type="password" placeholder="Password" value={authForm.password} onChange={(e) => setAuthForm((f) => ({ ...f, password: e.target.value }))} />
          <button onClick={handleLogin}>Login</button>
          <p>
            No account? <button className="link" onClick={() => setPage({ name: 'register' })}>Register</button>
          </p>
        </div>
      )
    }

    if (page.name === 'register') {
      return (
        <div className="auth-card">
          <h1>Register</h1>
          <input type="text" placeholder="Name" value={authForm.name} onChange={(e) => setAuthForm((f) => ({ ...f, name: e.target.value }))} />
          <input type="email" placeholder="Email" value={authForm.email} onChange={(e) => setAuthForm((f) => ({ ...f, email: e.target.value }))} />
          <input type="password" placeholder="Password" value={authForm.password} onChange={(e) => setAuthForm((f) => ({ ...f, password: e.target.value }))} />
          <button onClick={handleRegister}>Register</button>
          <p>
            Have account? <button className="link" onClick={() => setPage({ name: 'login' })}>Login</button>
          </p>
        </div>
      )
    }

    return <p>Page not found</p>
  }

  return (
    <div className="page">
      <header className="topbar">
        <div className="logo">Hotel Management</div>
        <nav>
          {authUser && authUser.role === 'ADMIN' && (
            <>
              <button onClick={() => setPage({ name: 'admin-dashboard' })}>Admin Dashboard</button>
              <button onClick={() => setPage({ name: 'admin-users' })}>Manage Users</button>
              <button onClick={() => setPage({ name: 'admin-hotels' })}>Hotel Approvals</button>
              <button onClick={() => setPage({ name: 'admin-bookings' })}>Bookings</button>
            </>
          )}
          {authUser && authUser.role === 'HOTEL_OWNER' && (
            <>
              <button onClick={() => setPage({ name: 'owner-dashboard' })}>Owner Dashboard</button>
              <button onClick={() => setPage({ name: 'owner-hotels' })}>My Hotels</button>
              <button onClick={() => setPage({ name: 'owner-rooms' })}>Manage Rooms</button>
              <button onClick={() => setPage({ name: 'owner-bookings' })}>Bookings</button>
            </>
          )}
          {authUser && authUser.role === 'USER' && (
            <>
              <button onClick={() => setPage({ name: 'home' })}>Home</button>
              <button onClick={() => setPage({ name: 'my-bookings' })}>My Bookings</button>
              <button onClick={() => setPage({ name: 'profile' })}>Profile</button>
            </>
          )}
          {authUser && <button onClick={handleLogout}>Logout</button>}
        </nav>
      </header>
      <main>{pageContent()}</main>
      <footer>
        <p>Frontend implementation (dark theme) based on hotel.txt requirements</p>
      </footer>
    </div>
  )
}

export default App
