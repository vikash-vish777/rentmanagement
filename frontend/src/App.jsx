import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import './App.css';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
const API_ORIGIN = API_BASE.replace(/\/api\/?$/, '');
const FALLBACK_IMAGE =
  'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=85';
const imageSource = (url) => url?.startsWith('/') ? `${API_ORIGIN}${url}` : url || FALLBACK_IMAGE;
const CURRENT_YEAR = new Date().getFullYear();
const money = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 0,
});
const roleName = (role = '') => role.replace('ROLE_', '').toLowerCase();
const today = new Date().toISOString().slice(0, 10);
const emptyProperty = {
  title: '', description: '', propertyType: 'APARTMENT', rent: '',
  securityDeposit: '', address: '', city: '', area: '', pincode: '',
  latitude: '', longitude: '', bedrooms: 1, bathrooms: 1, sqft: '',
  furnishedStatus: 'UNFURNISHED', availableFrom: today,
};

async function api(path, { token, ...options } = {}) {
  const headers = new Headers(options.headers || {});
  if (token) headers.set('Authorization', `Bearer ${token}`);
  if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  } catch {
    throw new Error(`Can't reach RentEase API at ${API_BASE}. Start the backend and try again.`);
  }

  const text = await response.text();
  let data;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }
  if (!response.ok) {
    const message = data?.message || data?.error || (typeof data === 'string' ? data : '');
    throw new Error(message || `Request failed (${response.status})`);
  }
  return data;
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('rentEaseToken') || '');
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('rentEaseUser') || 'null');
    } catch {
      return null;
    }
  });
  const [properties, setProperties] = useState([]);
  const [favorites, setFavorites] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [landlordProperties, setLandlordProperties] = useState([]);
  const [adminStats, setAdminStats] = useState(null);
  const [adminUsers, setAdminUsers] = useState([]);
  const [adminProperties, setAdminProperties] = useState([]);
  const [adminBookings, setAdminBookings] = useState([]);
  const [conversations, setConversations] = useState([]);
  const [chatMessages, setChatMessages] = useState([]);
  const [selectedPartner, setSelectedPartner] = useState(null);
  const [messageText, setMessageText] = useState('');
  const [selectedProperty, setSelectedProperty] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [compareIds, setCompareIds] = useState([]);
  const [filters, setFilters] = useState({
    city: '', area: '', minRent: '', maxRent: '', propertyType: '',
    bedrooms: '', furnishedStatus: '',
  });
  const [sort, setSort] = useState('recommended');
  const [tab, setTab] = useState('discover');
  const [adminTab, setAdminTab] = useState('overview');
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState('login');
  const [authForm, setAuthForm] = useState({
    name: '', email: '', phone: '', password: '', role: 'ROLE_TENANT',
  });
  const [propertyDialog, setPropertyDialog] = useState(false);
  const [propertyForm, setPropertyForm] = useState(emptyProperty);
  const [editingProperty, setEditingProperty] = useState(null);
  const [imageFiles, setImageFiles] = useState([]);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [loading, setLoading] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const propertyRequestId = useRef(0);

  const role = roleName(user?.role);
  const isLandlord = role === 'landlord';
  const isAdmin = role === 'admin';
  const isTenant = role === 'tenant';

  const loadProperties = useCallback(async () => {
    const requestId = ++propertyRequestId.current;
    setLoading(true);
    try {
      const params = new URLSearchParams();
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== '') params.set(key, value);
      });
      const query = params.toString();
      const result = await api(`/properties${query ? `/search?${query}` : ''}`);
      if (requestId !== propertyRequestId.current) return;
      setProperties(result);
      setError('');
    } catch (requestError) {
      if (requestId === propertyRequestId.current) setError(requestError.message);
    } finally {
      if (requestId === propertyRequestId.current) setLoading(false);
    }
  }, [filters]);

  const loadAccountData = useCallback(async () => {
    if (!token || !user) return;
    try {
      if (role === 'tenant') {
        const [saved, ownBookings] = await Promise.all([
          api('/favorites', { token }),
          api('/bookings/my-bookings', { token }),
        ]);
        setFavorites(saved);
        setBookings(ownBookings);
      } else if (role === 'landlord') {
        const [ownProperties, ownBookings] = await Promise.all([
          api('/properties/my-properties', { token }),
          api('/bookings/landlord', { token }),
        ]);
        setLandlordProperties(ownProperties);
        setBookings(ownBookings);
      } else if (role === 'admin') {
        const [stats, users, allProperties, allBookings] = await Promise.all([
          api('/admin/stats', { token }),
          api('/admin/users', { token }),
          api('/admin/properties', { token }),
          api('/admin/bookings', { token }),
        ]);
        setAdminStats(stats);
        setAdminUsers(users);
        setAdminProperties(allProperties);
        setAdminBookings(allBookings);
      }
    } catch (requestError) {
      setError(requestError.message);
    }
  }, [token, user, role]);

  useEffect(() => {
    const timer = window.setTimeout(() => { void loadProperties(); }, 250);
    return () => window.clearTimeout(timer);
  }, [loadProperties]);

  useEffect(() => {
    if (token) localStorage.setItem('rentEaseToken', token);
    else localStorage.removeItem('rentEaseToken');
    if (user) localStorage.setItem('rentEaseUser', JSON.stringify(user));
    else localStorage.removeItem('rentEaseUser');
  }, [token, user]);

  useEffect(() => {
    if (!token) return;
    void Promise.resolve().then(loadAccountData);
  }, [loadAccountData, token]);

  useEffect(() => {
    if (selectedProperty) {
      api(`/reviews/property/${selectedProperty.id}`)
        .then(setReviews)
        .catch((requestError) => setError(requestError.message));
    }
  }, [selectedProperty]);

  useEffect(() => {
    if (!token || tab !== 'messages') return;
    api('/messages/conversations', { token })
      .then((items) => {
        setConversations(items);
      })
      .catch((requestError) => setError(requestError.message));
  }, [tab, token, success]);

  useEffect(() => {
    if (!token || !selectedPartner) return;
    api(`/messages/chat/${selectedPartner.partnerId}`, { token })
      .then(setChatMessages)
      .catch((requestError) => setError(requestError.message));
  }, [token, selectedPartner]);

  const visibleProperties = useMemo(() => {
    const items = [...properties];
    if (sort === 'rent-low') items.sort((a, b) => a.rent - b.rent);
    if (sort === 'rent-high') items.sort((a, b) => b.rent - a.rent);
    if (sort === 'newest') items.sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''));
    return items;
  }, [properties, sort]);

  function notify(message) {
    setSuccess(message);
    setError('');
    window.setTimeout(() => setSuccess(''), 3500);
  }

  async function submitAuth(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      if (authMode === 'register') {
        await api('/auth/register', {
          method: 'POST',
          body: JSON.stringify(authForm),
        });
        setAuthMode('login');
        setAuthForm((form) => ({ ...form, password: '' }));
        notify('Account created. Sign in to start exploring.');
        return;
      }
      const result = await api('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email: authForm.email, password: authForm.password }),
      });
      const signedInUser = {
        id: result.id, name: result.name, email: result.email,
        phone: result.phone, role: result.role,
      };
      setToken(result.token);
      setUser(signedInUser);
      setAuthOpen(false);
      setAuthForm({ name: '', email: '', phone: '', password: '', role: 'ROLE_TENANT' });
      notify(`Welcome back, ${result.name.split(' ')[0]}.`);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  function logout() {
    setToken('');
    setUser(null);
    setFavorites([]);
    setBookings([]);
    setLandlordProperties([]);
    setTab('discover');
    notify('You are signed out.');
  }

  async function toggleFavorite(property) {
    if (!token) {
      setAuthOpen(true);
      return;
    }
    try {
      const saved = favorites.some((item) => item.id === property.id);
      await api(`/favorites/${property.id}`, { token, method: saved ? 'DELETE' : 'POST' });
      setFavorites((items) => saved
        ? items.filter((item) => item.id !== property.id)
        : [...items, property]);
      notify(saved ? 'Removed from your saved homes.' : 'Home saved to your wishlist.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function createBooking(event) {
    event.preventDefault();
    if (!token) {
      setAuthOpen(true);
      return;
    }
    const form = new FormData(event.currentTarget);
    try {
      await api('/bookings', {
        token,
        method: 'POST',
        body: JSON.stringify({
          propertyId: selectedProperty.id,
          moveInDate: form.get('moveInDate'),
          message: form.get('message'),
        }),
      });
      await loadAccountData();
      notify('Booking request sent to the landlord.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  function startConversation(property) {
    if (!token) {
      setAuthOpen(true);
      return;
    }
    if (!property.owner) {
      setError('Landlord contact details are not available for this listing.');
      return;
    }
    setSelectedPartner({
      partnerId: property.owner.id,
      partnerName: property.owner.name,
      partnerEmail: property.owner.email,
    });
    setSelectedProperty(null);
    setTab('messages');
  }

  async function sendMessage(event) {
    event.preventDefault();
    if (!messageText.trim() || !selectedPartner) return;
    try {
      const sent = await api('/messages', {
        token,
        method: 'POST',
        body: JSON.stringify({
          receiverId: selectedPartner.partnerId,
          propertyId: selectedProperty?.id || null,
          content: messageText.trim(),
        }),
      });
      setChatMessages((messages) => [...messages, sent]);
      setMessageText('');
      notify('Message sent.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  function openNewProperty(property = null) {
    setEditingProperty(property);
    setImageFiles([]);
    setPropertyForm(property
      ? {
          title: property.title || '', description: property.description || '',
          propertyType: property.propertyType || 'APARTMENT', rent: property.rent || '',
          securityDeposit: property.securityDeposit || '', address: property.address || '',
          city: property.city || '', area: property.area || '', pincode: property.pincode || '',
          latitude: property.latitude ?? '', longitude: property.longitude ?? '',
          bedrooms: property.bedrooms || 1, bathrooms: property.bathrooms || 1,
          sqft: property.sqft || '', furnishedStatus: property.furnishedStatus || 'UNFURNISHED',
          availableFrom: property.availableFrom || today,
        }
      : { ...emptyProperty });
    setPropertyDialog(true);
  }

  async function saveProperty(event) {
    event.preventDefault();
    setBusy(true);
    try {
      const payload = {
        ...propertyForm,
        rent: Number(propertyForm.rent),
        securityDeposit: Number(propertyForm.securityDeposit),
        bedrooms: Number(propertyForm.bedrooms),
        bathrooms: Number(propertyForm.bathrooms),
        sqft: propertyForm.sqft ? Number(propertyForm.sqft) : null,
        latitude: propertyForm.latitude ? Number(propertyForm.latitude) : null,
        longitude: propertyForm.longitude ? Number(propertyForm.longitude) : null,
      };
      const response = await api(
        editingProperty ? `/properties/${editingProperty.id}` : '/properties',
        { token, method: editingProperty ? 'PUT' : 'POST', body: JSON.stringify(payload) },
      );
      const saved = response.data || response;
      if (imageFiles.length) {
        const form = new FormData();
        imageFiles.forEach((file) => form.append('files', file));
        await api(`/properties/${saved.id}/images`, { token, method: 'POST', body: form });
      }
      setPropertyDialog(false);
      await Promise.all([loadAccountData(), loadProperties()]);
      notify(editingProperty ? 'Your listing has been updated.' : 'Your property is now listed.');
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  async function deleteProperty(property) {
    if (!window.confirm(`Delete "${property.title}"? This cannot be undone.`)) return;
    try {
      await api(`/properties/${property.id}`, { token, method: 'DELETE' });
      await Promise.all([loadAccountData(), loadProperties()]);
      notify('Listing deleted.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function updatePropertyStatus(property, status) {
    try {
      await api(`/properties/${property.id}/status?status=${status}`, { token, method: 'PATCH' });
      await Promise.all([loadAccountData(), loadProperties()]);
      notify('Availability updated.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function updateBooking(booking, status) {
    try {
      await api(`/bookings/${booking.id}/status`, {
        token,
        method: 'PUT',
        body: JSON.stringify({ status, landlordNote: '' }),
      });
      await Promise.all([loadAccountData(), loadProperties()]);
      notify(`Booking ${status.toLowerCase()}.`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function cancelBooking(booking) {
    try {
      await api(`/bookings/${booking.id}/cancel`, { token, method: 'PUT' });
      await Promise.all([loadAccountData(), loadProperties()]);
      notify('Booking cancelled.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function toggleUser(userToUpdate) {
    try {
      await api(`/admin/users/${userToUpdate.id}/status`, {
        token, method: 'PUT', body: JSON.stringify({ active: !userToUpdate.active }),
      });
      await loadAccountData();
      notify(`${userToUpdate.name}'s account ${userToUpdate.active ? 'suspended' : 'reactivated'}.`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function deleteUser(userToDelete) {
    if (!window.confirm(`Permanently delete ${userToDelete.name}'s account?`)) return;
    try {
      await api(`/admin/users/${userToDelete.id}`, { token, method: 'DELETE' });
      await loadAccountData();
      notify(`${userToDelete.name}'s account has been deleted.`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function moderateProperty(property, status) {
    try {
      await api(`/admin/properties/${property.id}/status?status=${status}`, { token, method: 'PUT' });
      await loadAccountData();
      notify(`Listing marked ${status.toLowerCase().replace('_', ' ')}.`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function deleteAdminProperty(property) {
    if (!window.confirm(`Permanently delete "${property.title}"?`)) return;
    try {
      await api(`/admin/properties/${property.id}`, { token, method: 'DELETE' });
      await loadAccountData();
      notify('Listing deleted.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function deletePropertyImage(property, image) {
    try {
      await api(`/properties/${property.id}/images/${image.id}`, { token, method: 'DELETE' });
      setEditingProperty((current) => current?.id === property.id
        ? { ...current, images: current.images.filter((item) => item.id !== image.id) }
        : current);
      await loadAccountData();
      notify('Photo removed from listing.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function submitReview(event) {
    event.preventDefault();
    try {
      await api('/reviews', {
        token,
        method: 'POST',
        body: JSON.stringify({
          propertyId: selectedProperty.id,
          rating: Number(reviewForm.rating),
          comment: reviewForm.comment,
        }),
      });
      const updated = await api(`/reviews/property/${selectedProperty.id}`);
      setReviews(updated);
      setReviewForm({ rating: 5, comment: '' });
      notify('Thanks for sharing your review.');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  function toggleCompare(id) {
    setCompareIds((items) => {
      if (items.includes(id)) return items.filter((item) => item !== id);
      if (items.length >= 3) {
        setError('Compare up to three homes at a time.');
        return items;
      }
      return [...items, id];
    });
  }

  const navItems = [
    { id: 'discover', label: 'Discover', icon: '⌕' },
    ...(user ? [{ id: 'dashboard', label: 'Dashboard', icon: '▦' }] : []),
    ...(isTenant ? [{ id: 'favorites', label: 'Saved homes', icon: '♡' }] : []),
    ...(user ? [{ id: 'messages', label: 'Messages', icon: '✉' }] : []),
  ];

  return (
    <div className="app-shell">
      <header className="site-header">
        <a className="brand" href="#discover" onClick={() => setTab('discover')}>
          <span className="brand-mark">r</span>
          <span>rent<span className="brand-light">ease</span></span>
        </a>
        <nav className="main-nav" aria-label="Main navigation">
          {navItems.map((item) => (
            <button
              className={`nav-link ${tab === item.id ? 'selected' : ''}`}
              key={item.id}
              onClick={() => setTab(item.id)}
              type="button"
            >
              <span aria-hidden="true">{item.icon}</span>{item.label}
              {item.id === 'messages' && conversations.some((chat) => chat.unreadCount > 0)
                ? <i className="unread-dot" /> : null}
            </button>
          ))}
        </nav>
        <div className="header-actions">
          {user ? (
            <>
              <span className="avatar">{user.name?.slice(0, 1).toUpperCase()}</span>
              <div className="account-summary">
                <strong>{user.name}</strong><small>{role}</small>
              </div>
              <button className="button button-light button-small" onClick={logout} type="button">Sign out</button>
            </>
          ) : (
            <>
              <button className="button button-light button-small" onClick={() => { setAuthMode('login'); setAuthOpen(true); }} type="button">Log in</button>
              <button className="button button-dark button-small" onClick={() => { setAuthMode('register'); setAuthOpen(true); }} type="button">Get started <span>↗</span></button>
            </>
          )}
        </div>
      </header>

      {error && <Notice kind="error" message={error} onClose={() => setError('')} />}
      {success && <Notice kind="success" message={success} onClose={() => setSuccess('')} />}
      <main>
        {tab === 'discover' && (
          <>
            <section className="hero">
              <div className="hero-copy">
                <p className="eyebrow"><span className="eyebrow-dot" /> A better way to rent</p>
                <h1>Find a place<br />to <em>feel at home.</em></h1>
                <p className="hero-subtitle">Thoughtful homes. Trusted landlords. Your next chapter starts here.</p>
                <div className="hero-proof"><span className="proof-mark">⌂</span><span>Homes for every next chapter</span></div>
              </div>
              <div className="hero-image" role="img" aria-label="A sunlit modern home">
                <div className="hero-image-note"><span>⌖</span><div><strong>Find your own corner</strong><small>Homes picked for real life</small></div></div>
                <span className="hero-image-index">01 / 04</span>
              </div>
            </section>
            <section className="search-wrap" aria-label="Search homes">
              <div className="search-title"><span className="search-spark">✳</span><div><strong>Where would you like to live?</strong><small>Make yourself at home</small></div></div>
              <label className="search-field"><span>⌖</span><div><small>City</small><input placeholder="e.g. Nashik" value={filters.city} onChange={(e) => setFilters({ ...filters, city: e.target.value })} /></div></label>
              <label className="search-field"><span>⌂</span><div><small>Neighbourhood</small><input placeholder="Any area" value={filters.area} onChange={(e) => setFilters({ ...filters, area: e.target.value })} /></div></label>
              <label className="search-field"><span>₹</span><div><small>Monthly budget</small><select value={filters.maxRent} onChange={(e) => setFilters({ ...filters, maxRent: e.target.value })}><option value="">Any budget</option><option value="10000">Up to ₹10,000</option><option value="20000">Up to ₹20,000</option><option value="35000">Up to ₹35,000</option><option value="50000">Up to ₹50,000</option></select></div></label>
              <button className="button button-dark search-button" onClick={loadProperties} type="button"><span>⌕</span> Search homes</button>
            </section>
            <section className="listing-section">
              <div className="section-topline">
                <div><p className="eyebrow">THE COLLECTION</p><h2>Homes worth coming back to</h2><p className="section-note">Explore places with room for all the things you love.</p></div>
                <div className="listing-tools">
                  <span className="result-count">{visibleProperties.length} homes</span>
                  <label className="sort-control"><span>Sort by</span><select value={sort} onChange={(e) => setSort(e.target.value)}><option value="recommended">Recommended</option><option value="rent-low">Rent: low to high</option><option value="rent-high">Rent: high to low</option><option value="newest">Newest</option></select></label>
                </div>
              </div>
              <div className="filter-row">
                <label><span>Property type</span><select value={filters.propertyType} onChange={(e) => setFilters({ ...filters, propertyType: e.target.value })}><option value="">All homes</option><option value="APARTMENT">Apartment</option><option value="INDEPENDENT_HOUSE">Independent house</option><option value="STUDIO">Studio</option><option value="VILLA">Villa</option><option value="ROOM">Room</option></select></label>
                <label><span>Bedrooms</span><select value={filters.bedrooms} onChange={(e) => setFilters({ ...filters, bedrooms: e.target.value })}><option value="">Any</option><option value="1">1+</option><option value="2">2+</option><option value="3">3+</option><option value="4">4+</option></select></label>
                <label><span>Furnishing</span><select value={filters.furnishedStatus} onChange={(e) => setFilters({ ...filters, furnishedStatus: e.target.value })}><option value="">Any</option><option value="FURNISHED">Furnished</option><option value="SEMI_FURNISHED">Semi-furnished</option><option value="UNFURNISHED">Unfurnished</option></select></label>
                <label><span>Min rent</span><input inputMode="numeric" placeholder="₹ 0" value={filters.minRent} onChange={(e) => setFilters({ ...filters, minRent: e.target.value })} /></label>
                <button className="text-button" onClick={() => setFilters({ city: '', area: '', minRent: '', maxRent: '', propertyType: '', bedrooms: '', furnishedStatus: '' })} type="button">Clear filters</button>
              </div>
              {loading ? <div className="loading-grid">{[1, 2, 3].map((item) => <div className="skeleton-card" key={item} />)}</div> : (
                visibleProperties.length ? (
                  <div className="property-grid">
                    {visibleProperties.map((property) => (
                      <PropertyCard
                        key={property.id}
                        property={property}
                        saved={favorites.some((item) => item.id === property.id) || property.favorite}
                        compared={compareIds.includes(property.id)}
                        onOpen={() => setSelectedProperty(property)}
                        onFavorite={() => toggleFavorite(property)}
                        onCompare={() => toggleCompare(property.id)}
                      />
                    ))}
                  </div>
                ) : <EmptyState title="No homes found just yet" detail="Try widening your search or clearing a filter." action={() => setFilters({ city: '', area: '', minRent: '', maxRent: '', propertyType: '', bedrooms: '', furnishedStatus: '' })} actionLabel="Clear search" />
              )}
            </section>
          </>
        )}

        {tab === 'dashboard' && isLandlord && (
          <LandlordDashboard
            properties={landlordProperties}
            bookings={bookings}
            onAdd={() => openNewProperty()}
            onEdit={openNewProperty}
            onDelete={deleteProperty}
            onStatus={updatePropertyStatus}
            onBooking={updateBooking}
            onReload={loadAccountData}
          />
        )}
        {tab === 'dashboard' && isTenant && (
          <TenantDashboard
            bookings={bookings}
            favorites={favorites}
            onOpen={(property) => setSelectedProperty(property)}
            onCancel={cancelBooking}
            onDiscover={() => setTab('discover')}
          />
        )}
        {tab === 'dashboard' && isAdmin && (
          <AdminDashboard
            stats={adminStats}
            users={adminUsers}
            properties={adminProperties}
            bookings={adminBookings}
            activeTab={adminTab}
            onTab={setAdminTab}
            onUser={toggleUser}
            onDeleteUser={deleteUser}
            onProperty={moderateProperty}
            onDeleteProperty={deleteAdminProperty}
            onReload={loadAccountData}
          />
        )}
        {tab === 'favorites' && (
          <section className="workspace-page">
            <PageHeading eyebrow="YOUR SHORTLIST" title="Saved for later" detail="The places that caught your eye, all in one spot." />
            {favorites.length
              ? <div className="property-grid">{favorites.map((property) => <PropertyCard key={property.id} property={property} saved onOpen={() => setSelectedProperty(property)} onFavorite={() => toggleFavorite(property)} compared={compareIds.includes(property.id)} onCompare={() => toggleCompare(property.id)} />)}</div>
              : <EmptyState title="Your shortlist is waiting" detail="Tap the heart on a home to keep it close." action={() => setTab('discover')} actionLabel="Explore homes" />}
          </section>
        )}
        {tab === 'messages' && (
          <MessagesPanel
            conversations={conversations}
            selected={selectedPartner}
            messages={chatMessages}
            currentUser={user}
            text={messageText}
            onText={setMessageText}
            onSelect={setSelectedPartner}
            onSend={sendMessage}
          />
        )}
      </main>

      <footer className="site-footer"><a className="brand footer-brand" href="#discover" onClick={() => setTab('discover')}><span className="brand-mark">r</span><span>rent<span className="brand-light">ease</span></span></a><span>A little closer to home.</span><span>© {CURRENT_YEAR} RentEase</span></footer>

      {compareIds.length > 0 && (
        <div className="compare-bar">
          <div><strong>{compareIds.length} {compareIds.length === 1 ? 'home' : 'homes'} selected</strong><span>Compare the details side by side</span></div>
          <button className="button button-light button-small" onClick={() => setCompareIds([])} type="button">Clear</button>
          <button className="button button-dark button-small" onClick={() => setTab('compare')} type="button">Compare homes ↗</button>
        </div>
      )}
      {tab === 'compare' && (
        <CompareDialog
          properties={[...properties, ...favorites, ...landlordProperties, ...adminProperties]
            .filter((item, index, all) => compareIds.includes(item.id) && all.findIndex((candidate) => candidate.id === item.id) === index)}
          onClose={() => setTab('discover')}
          onRemove={toggleCompare}
          onOpen={(property) => setSelectedProperty(property)}
        />
      )}
      {selectedProperty && (
        <>
          <PropertyDialog
            property={selectedProperty}
            saved={favorites.some((item) => item.id === selectedProperty.id)}
            reviews={reviews}
            user={user}
            reviewForm={reviewForm}
            setReviewForm={setReviewForm}
            onClose={() => setSelectedProperty(null)}
            onFavorite={() => toggleFavorite(selectedProperty)}
            onBook={createBooking}
            onMessage={() => startConversation(selectedProperty)}
            onAuth={() => { setAuthMode('login'); setAuthOpen(true); }}
            onReview={submitReview}
          />
          {isTenant && (
            <button
              className="message-landlord-fab"
              onClick={() => startConversation(selectedProperty)}
              type="button"
            >
              <span aria-hidden="true">✉</span> Message landlord
            </button>
          )}
        </>
      )}
      {propertyDialog && (
        <PropertyFormDialog
          form={propertyForm}
          setForm={setPropertyForm}
          files={imageFiles}
          setFiles={setImageFiles}
          editing={editingProperty}
          busy={busy}
          onClose={() => setPropertyDialog(false)}
          onSubmit={saveProperty}
          onDeleteImage={deletePropertyImage}
        />
      )}
      {authOpen && (
        <AuthDialog
          mode={authMode}
          setMode={setAuthMode}
          form={authForm}
          setForm={setAuthForm}
          error={error}
          busy={busy}
          onClose={() => { setAuthOpen(false); setError(''); }}
          onSubmit={submitAuth}
        />
      )}
    </div>
  );
}

function Notice({ kind, message, onClose }) {
  return <div className={`notice notice-${kind}`} role="status"><span>{kind === 'error' ? '!' : '✓'}</span>{message}<button aria-label="Dismiss message" onClick={onClose} type="button">×</button></div>;
}

function PageHeading({ eyebrow, title, detail, action }) {
  return <div className="page-heading"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1><p>{detail}</p></div>{action}</div>;
}

function EmptyState({ title, detail, action, actionLabel }) {
  return <div className="empty-panel"><span className="empty-icon">⌂</span><h3>{title}</h3><p>{detail}</p>{action && <button className="button button-dark" onClick={action} type="button">{actionLabel}</button>}</div>;
}

function PropertyCard({ property, saved, compared, onOpen, onFavorite, onCompare }) {
  const image = imageSource(property.images?.[0]?.imageUrl);
  return (
    <article className="property-card">
      <button className="card-image-button" onClick={onOpen} type="button" aria-label={`View ${property.title}`}>
        <img src={image} alt={property.title} loading="lazy" onError={(event) => { event.currentTarget.src = FALLBACK_IMAGE; }} />
        <span className={`status-badge status-${property.status?.toLowerCase()}`}>{property.status === 'AVAILABLE' ? 'Available now' : (property.status || '').replace('_', ' ')}</span>
      </button>
      <button className={`favorite-button ${saved ? 'is-saved' : ''}`} onClick={onFavorite} aria-label={saved ? 'Remove from saved homes' : 'Save this home'} type="button">{saved ? '♥' : '♡'}</button>
      <div className="property-card-body">
        <div className="card-location"><span>⌖</span> {property.area}, {property.city}</div>
        <button className="card-title" onClick={onOpen} type="button">{property.title}</button>
        <div className="card-meta"><span>{property.bedrooms} bed</span><i>·</i><span>{property.bathrooms} bath</span><i>·</i><span>{property.sqft ? `${property.sqft} sq ft` : property.propertyType?.replace('_', ' ').toLowerCase()}</span></div>
        <div className="card-bottom"><p><strong>{money.format(property.rent)}</strong><span> / month</span></p><button className={`compare-button ${compared ? 'compared' : ''}`} onClick={onCompare} type="button">{compared ? '✓ Added' : '＋ Compare'}</button></div>
      </div>
    </article>
  );
}

function LandlordDashboard({ properties, bookings, onAdd, onEdit, onDelete, onStatus, onBooking, onReload }) {
  const pending = bookings.filter((booking) => booking.status === 'PENDING');
  const available = properties.filter((property) => property.status === 'AVAILABLE').length;
  return (
    <section className="workspace-page">
      <PageHeading eyebrow="LANDLORD STUDIO" title="Your rental portfolio" detail="Keep your homes and incoming requests in one place." action={<button className="button button-dark" onClick={onAdd} type="button"><span>＋</span> Add a listing</button>} />
      <div className="metric-grid">
        <Metric label="Total properties" value={properties.length} icon="⌂" />
        <Metric label="Available homes" value={available} icon="✳" />
        <Metric label="Booking requests" value={pending.length} icon="↗" highlight />
        <Metric label="Total bookings" value={bookings.length} icon="▤" />
      </div>
      <div className="dashboard-columns">
        <section className="panel">
          <div className="panel-heading"><div><p className="eyebrow">YOUR LISTINGS</p><h2>Property manager</h2></div><button className="text-button" onClick={onReload} type="button">Refresh ↻</button></div>
          {properties.length ? <div className="managed-list">{properties.map((property) => (
            <div className="managed-property" key={property.id}>
              <img src={imageSource(property.images?.[0]?.imageUrl)} alt="" onError={(event) => { event.currentTarget.src = FALLBACK_IMAGE; }} />
              <div className="managed-info"><h3>{property.title}</h3><p>{property.area}, {property.city} <span>·</span> {money.format(property.rent)}/mo</p><span className={`status-label status-${property.status?.toLowerCase()}`}>{(property.status || '').replace('_', ' ')}</span></div>
              <div className="managed-actions"><button className="button button-light button-small" onClick={() => onEdit(property)} type="button">Edit</button><button className="button button-danger button-small" onClick={() => onDelete(property)} type="button">Delete</button><select aria-label="Change availability" value={property.status} onChange={(event) => onStatus(property, event.target.value)}><option value="AVAILABLE">Available</option><option value="RENTED">Rented</option><option value="UNDER_MAINTENANCE">Maintenance</option></select></div>
            </div>
          ))}</div> : <EmptyState title="Your next listing starts here" detail="Add a home to meet your next great tenant." action={onAdd} actionLabel="Add your first listing" />}
        </section>
        <BookingPanel bookings={bookings} mode="landlord" onUpdate={onBooking} />
      </div>
    </section>
  );
}

function TenantDashboard({ bookings, favorites, onOpen, onCancel, onDiscover }) {
  return (
    <section className="workspace-page">
      <PageHeading eyebrow="TENANT SPACE" title="Your home journey" detail="Keep track of requests, plans and places you've saved." action={<button className="button button-dark" onClick={onDiscover} type="button">Explore homes ↗</button>} />
      <div className="metric-grid">
        <Metric label="Booking requests" value={bookings.length} icon="▤" />
        <Metric label="Awaiting a reply" value={bookings.filter((item) => item.status === 'PENDING').length} icon="◷" highlight />
        <Metric label="Saved homes" value={favorites.length} icon="♡" />
        <Metric label="Confirmed stays" value={bookings.filter((item) => ['ACCEPTED', 'CONFIRMED'].includes(item.status)).length} icon="✓" />
      </div>
      <div className="dashboard-columns tenant-columns">
        <BookingPanel bookings={bookings} mode="tenant" onCancel={onCancel} onOpen={onOpen} />
        <section className="panel saved-preview"><div className="panel-heading"><div><p className="eyebrow">YOUR SHORTLIST</p><h2>Saved homes</h2></div><span className="inline-count">{favorites.length}</span></div>
          {favorites.length ? favorites.slice(0, 4).map((property) => <button className="saved-row" key={property.id} onClick={() => onOpen(property)} type="button"><img src={imageSource(property.images?.[0]?.imageUrl)} alt="" onError={(event) => { event.currentTarget.src = FALLBACK_IMAGE; }} /><span><strong>{property.title}</strong><small>{property.area}, {property.city}</small></span><b>{money.format(property.rent)}</b></button>) : <p className="muted-copy">Save homes you love and they'll be right here.</p>}
        </section>
      </div>
    </section>
  );
}

function BookingPanel({ bookings, mode, onUpdate, onCancel, onOpen }) {
  return (
    <section className="panel">
      <div className="panel-heading"><div><p className="eyebrow">{mode === 'landlord' ? 'INCOMING' : 'YOUR ACTIVITY'}</p><h2>{mode === 'landlord' ? 'Booking requests' : 'Booking history'}</h2></div><span className="inline-count">{bookings.length}</span></div>
      {bookings.length ? <div className="booking-list">{bookings.map((booking) => (
        <article className="booking-row" key={booking.id}>
          <div className="booking-symbol">{mode === 'landlord' ? booking.tenant?.name?.slice(0, 1) : '⌂'}</div>
          <div className="booking-content">
            <strong>{mode === 'landlord' ? booking.tenant?.name : booking.property?.title}</strong>
            <span>{mode === 'landlord' ? booking.property?.title : `${booking.property?.area}, ${booking.property?.city}`}</span>
            <small>Move-in {booking.moveInDate || 'date not set'} <i>·</i> {money.format(booking.property?.rent || 0)}/mo</small>
            {booking.message && <p className="booking-message">“{booking.message}”</p>}
            {booking.landlordNote && <p className="booking-message">Landlord: {booking.landlordNote}</p>}
          </div>
          <div className="booking-controls">
            <span className={`status-label status-${booking.status?.toLowerCase()}`}>{booking.status}</span>
            {mode === 'landlord' && booking.status === 'PENDING' && <><button className="button button-dark button-small" onClick={() => onUpdate(booking, 'ACCEPTED')} type="button">Accept</button><button className="button button-light button-small" onClick={() => onUpdate(booking, 'REJECTED')} type="button">Decline</button></>}
            {mode === 'tenant' && ['PENDING', 'ACCEPTED', 'CONFIRMED'].includes(booking.status) && <button className="text-button" onClick={() => onCancel(booking)} type="button">Cancel request</button>}
            {onOpen && <button className="text-button" onClick={() => onOpen(booking.property)} type="button">View home</button>}
          </div>
        </article>
      ))}</div> : <p className="muted-copy">Nothing here yet. New activity will show up here.</p>}
    </section>
  );
}

function Metric({ label, value, icon, highlight }) {
  return <div className={`metric-card ${highlight ? 'metric-highlight' : ''}`}><span className="metric-icon">{icon}</span><span className="metric-value">{value ?? '—'}</span><span className="metric-label">{label}</span></div>;
}

function AdminDashboard({ stats, users, properties, bookings, activeTab, onTab, onUser, onDeleteUser, onProperty, onDeleteProperty, onReload }) {
  return (
    <section className="workspace-page">
      <PageHeading eyebrow="PLATFORM ADMIN" title="The big picture" detail="A clear view of the people and places that make RentEase." action={<button className="button button-light" onClick={onReload} type="button">Refresh data ↻</button>} />
      <div className="metric-grid admin-metrics">
        <Metric label="Registered users" value={stats?.totalUsers} icon="♙" />
        <Metric label="Active listings" value={stats?.activeProperties} icon="⌂" />
        <Metric label="Booking requests" value={stats?.totalBookings} icon="▤" />
        <Metric label="Average monthly rent" value={stats?.averageRent != null ? money.format(stats.averageRent) : '—'} icon="₹" highlight />
      </div>
      <div className="admin-panel panel">
        <div className="admin-tabs">{[['overview', 'Overview'], ['users', `People · ${users.length}`], ['properties', `Listings · ${properties.length}`], ['bookings', `Bookings · ${bookings.length}`]].map(([key, label]) => <button className={activeTab === key ? 'active' : ''} key={key} onClick={() => onTab(key)} type="button">{label}</button>)}</div>
        {activeTab === 'overview' && (
          <div className="admin-overview">
            <div><p className="eyebrow">COMMUNITY</p><h3>Growing together</h3><div className="role-stats"><span>Tenants <b>{stats?.totalTenants ?? 0}</b></span><span>Landlords <b>{stats?.totalLandlords ?? 0}</b></span></div></div>
            <div className="city-breakdown"><p className="eyebrow">HOMES BY CITY</p>{Object.entries(stats?.propertiesByCity || {}).length ? Object.entries(stats.propertiesByCity).sort((a, b) => b[1] - a[1]).map(([city, count]) => <div className="city-row" key={city}><span>{city}</span><div><i style={{ width: `${Math.min(100, count / Math.max(1, stats.totalProperties) * 100)}%` }} /></div><b>{count}</b></div>) : <p className="muted-copy">City insights appear once listings are added.</p>}</div>
            <div className="admin-callout"><strong>{stats?.pendingBookings ?? 0}</strong><span>requests waiting for a landlord</span></div>
          </div>
        )}
        {activeTab === 'users' && <div className="table-wrap"><table><thead><tr><th>Member</th><th>Role</th><th>Phone</th><th>Joined</th><th>Account</th><th /></tr></thead><tbody>{users.map((item) => <tr key={item.id}><td><strong>{item.name}</strong><small>{item.email}</small></td><td><span className="role-chip">{roleName(item.role)}</span></td><td>{item.phone || '—'}</td><td>{item.createdAt ? item.createdAt.slice(0, 10) : '—'}</td><td><span className={`status-label ${item.active ? 'status-available' : 'status-rejected'}`}>{item.active ? 'Active' : 'Suspended'}</span></td><td>{roleName(item.role) !== 'admin' && <><button className="text-button" onClick={() => onUser(item)} type="button">{item.active ? 'Suspend' : 'Reactivate'}</button><button className="text-button danger-link" onClick={() => onDeleteUser(item)} type="button">Delete</button></>}</td></tr>)}</tbody></table></div>}
        {activeTab === 'properties' && <div className="table-wrap"><table><thead><tr><th>Property</th><th>Owner</th><th>Rent</th><th>Status</th><th>Moderation</th><th /></tr></thead><tbody>{properties.map((item) => <tr key={item.id}><td><strong>{item.title}</strong><small>{item.area}, {item.city}</small></td><td>{item.owner?.name || '—'}</td><td>{money.format(item.rent)}</td><td><span className={`status-label status-${item.status?.toLowerCase()}`}>{item.status?.replace('_', ' ')}</span></td><td><select value={item.status} onChange={(event) => onProperty(item, event.target.value)} aria-label={`Moderate ${item.title}`}><option value="AVAILABLE">Available</option><option value="PENDING_APPROVAL">Pending approval</option><option value="REJECTED">Rejected</option><option value="RENTED">Rented</option><option value="UNDER_MAINTENANCE">Maintenance</option></select></td><td><button className="text-button danger-link" onClick={() => onDeleteProperty(item)} type="button">Delete</button></td></tr>)}</tbody></table></div>}
        {activeTab === 'bookings' && <div className="table-wrap"><table><thead><tr><th>Property</th><th>Tenant</th><th>Landlord</th><th>Move-in</th><th>Status</th></tr></thead><tbody>{bookings.map((item) => <tr key={item.id}><td>{item.property?.title}</td><td>{item.tenant?.name}</td><td>{item.landlord?.name}</td><td>{item.moveInDate}</td><td><span className={`status-label status-${item.status?.toLowerCase()}`}>{item.status}</span></td></tr>)}</tbody></table></div>}
      </div>
    </section>
  );
}

function MessagesPanel({ conversations, selected, messages, currentUser, text, onText, onSelect, onSend }) {
  return (
    <section className="workspace-page">
      <PageHeading eyebrow="A LITTLE LESS DISTANT" title="Your conversations" detail="Good homes start with a good conversation." />
      <div className="messages-layout panel">
        <aside className="conversation-list"><div className="conversation-title">INBOX <span>{conversations.length}</span></div>{conversations.length ? conversations.map((chat) => <button className={`conversation-item ${selected?.partnerId === chat.partnerId ? 'active' : ''}`} key={chat.partnerId} onClick={() => onSelect(chat)} type="button"><span className="avatar">{chat.partnerName?.slice(0, 1)}</span><span className="conversation-copy"><strong>{chat.partnerName}</strong><small>{chat.lastMessage}</small></span>{chat.unreadCount > 0 && <i className="unread-count">{chat.unreadCount}</i>}</button>) : <p className="muted-copy conversation-empty">When you contact a landlord, your conversation will appear here.</p>}</aside>
        <section className="chat-panel">{selected ? <><header className="chat-header"><span className="avatar">{selected.partnerName?.slice(0, 1)}</span><div><strong>{selected.partnerName}</strong><small>{selected.partnerEmail}</small></div></header><div className="chat-messages">{messages.map((message) => <div className={`chat-bubble ${message.senderId === currentUser?.id ? 'sent' : ''}`} key={message.id}><p>{message.content}</p><small>{message.createdAt ? message.createdAt.replace('T', ' ').slice(0, 16) : 'Just now'}</small></div>)}{messages.length === 0 && <p className="muted-copy chat-empty">Say hello and start a conversation.</p>}</div><form className="chat-compose" onSubmit={onSend}><input value={text} onChange={(event) => onText(event.target.value)} placeholder="Write a thoughtful message..." aria-label="Message" required /><button className="button button-dark" type="submit">Send ↗</button></form></> : <div className="chat-placeholder"><span>✉</span><h3>A good conversation can make all the difference.</h3><p>Choose a conversation or contact a landlord from a home listing.</p></div>}</section>
      </div>
    </section>
  );
}

function CompareDialog({ properties, onClose, onRemove, onOpen }) {
  if (!properties.length) return <div className="modal-backdrop" onClick={onClose}><div className="modal-card compare-modal" onClick={(event) => event.stopPropagation()}><ModalHeader eyebrow="SIDE BY SIDE" title="Compare homes" onClose={onClose} /><EmptyState title="Choose homes to compare" detail="Select the compare button on a home card to add it here." /></div></div>;
  const gridStyle = { gridTemplateColumns: `140px repeat(${properties.length}, minmax(130px, 1fr))` };
  const rows = [
    ['Monthly rent', (item) => money.format(item.rent)],
    ['Security deposit', (item) => money.format(item.securityDeposit || 0)],
    ['Location', (item) => `${item.area}, ${item.city}`],
    ['Type', (item) => item.propertyType?.replaceAll('_', ' ')],
    ['Bedrooms', (item) => item.bedrooms],
    ['Bathrooms', (item) => item.bathrooms],
    ['Area', (item) => item.sqft ? `${item.sqft} sq ft` : '—'],
    ['Furnishing', (item) => item.furnishedStatus?.replaceAll('_', ' ')],
    ['Available from', (item) => item.availableFrom || 'Now'],
  ];
  return <div className="modal-backdrop" onClick={onClose}><div className="modal-card compare-modal" onClick={(event) => event.stopPropagation()}><ModalHeader eyebrow="SIDE BY SIDE" title="Compare homes" onClose={onClose} /><div className="compare-table"><div className="compare-table-row compare-table-header" style={gridStyle}><span>At a glance</span>{properties.map((item) => <div key={item.id}><img src={imageSource(item.images?.[0]?.imageUrl)} alt="" /><strong>{item.title}</strong><button className="text-button" onClick={() => onRemove(item.id)} type="button">Remove</button></div>)}</div>{rows.map(([label, getValue]) => <div className="compare-table-row" key={label} style={gridStyle}><strong>{label}</strong>{properties.map((item) => <span key={item.id}>{getValue(item)}</span>)}</div>)}</div><div className="compare-actions">{properties.map((item) => <button className="button button-light" key={item.id} onClick={() => onOpen(item)} type="button">View {item.title.slice(0, 20)} ↗</button>)}</div></div></div>;
}

function ModalHeader({ eyebrow, title, onClose }) {
  return <div className="modal-heading"><div><p className="eyebrow">{eyebrow}</p><h2>{title}</h2></div><button className="icon-button" onClick={onClose} aria-label="Close" type="button">×</button></div>;
}

function PropertyDialog({ property, saved, reviews, user, reviewForm, setReviewForm, onClose, onFavorite, onBook, onMessage, onAuth, onReview }) {
  const [imageIndex, setImageIndex] = useState(0);
  const images = property.images?.length ? property.images : [{ imageUrl: FALLBACK_IMAGE, caption: 'Home' }];
  const mapUrl = property.latitude && property.longitude
    ? `https://www.google.com/maps/search/?api=1&query=${property.latitude},${property.longitude}`
    : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${property.address}, ${property.city}`)}`;
  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card property-modal" onClick={(event) => event.stopPropagation()}>
        <button className="icon-button modal-close-float" onClick={onClose} aria-label="Close details" type="button">×</button>
        <div className="detail-gallery"><img className="detail-main-image" src={imageSource(images[imageIndex]?.imageUrl)} alt={images[imageIndex]?.caption || property.title} onError={(event) => { event.currentTarget.src = FALLBACK_IMAGE; }} />{images.length > 1 && <div className="gallery-thumbs">{images.map((image, index) => <button className={index === imageIndex ? 'active' : ''} key={image.id || image.imageUrl} onClick={() => setImageIndex(index)} type="button"><img src={imageSource(image.imageUrl)} alt={image.caption || `View ${index + 1}`} /></button>)}</div>}<span className="detail-counter">{String(imageIndex + 1).padStart(2, '0')} / {String(images.length).padStart(2, '0')}</span></div>
        <div className="detail-content"><div className="detail-main"><p className="eyebrow"><span className="eyebrow-dot" /> {property.area}, {property.city}</p><h2>{property.title}</h2><div className="detail-meta"><span>{property.bedrooms} bedrooms</span><i>·</i><span>{property.bathrooms} bathrooms</span><i>·</i><span>{property.sqft || '—'} sq ft</span></div><p className="detail-description">{property.description}</p><div className="detail-feature-row"><span><small>PROPERTY TYPE</small><strong>{property.propertyType?.replaceAll('_', ' ')}</strong></span><span><small>FURNISHING</small><strong>{property.furnishedStatus?.replaceAll('_', ' ')}</strong></span><span><small>AVAILABLE FROM</small><strong>{property.availableFrom || 'Immediately'}</strong></span></div><div className="address-card"><span>⌖</span><div><strong>{property.address}</strong><small>{property.area}, {property.city} {property.pincode}</small></div><a href={mapUrl} target="_blank" rel="noreferrer">Open map ↗</a></div><section className="review-section"><div className="panel-heading"><div><p className="eyebrow">COMMUNITY NOTES</p><h3>Reviews <span>({reviews.length})</span></h3></div></div>{reviews.map((review) => <article className="review-row" key={review.id}><span className="avatar">{review.tenant?.name?.slice(0, 1) || 'R'}</span><div><strong>{review.tenant?.name || 'RentEase renter'} <span>{'★'.repeat(review.rating)}</span></strong><p>{review.comment}</p></div></article>)}{user && roleName(user.role) === 'tenant' && <form className="review-form" onSubmit={onReview}><select aria-label="Your rating" value={reviewForm.rating} onChange={(event) => setReviewForm({ ...reviewForm, rating: event.target.value })}>{[5, 4, 3, 2, 1].map((item) => <option key={item} value={item}>{item} star{item > 1 ? 's' : ''}</option>)}</select><input placeholder="Share a helpful note..." value={reviewForm.comment} onChange={(event) => setReviewForm({ ...reviewForm, comment: event.target.value })} required /><button className="button button-light button-small" type="submit">Post review</button></form>}</section></div>
          <aside className="detail-sidebar"><div className="price-card"><p>Monthly rent</p><strong>{money.format(property.rent)}<small> / mo</small></strong><div className="deposit-line"><span>Security deposit</span><b>{money.format(property.securityDeposit || 0)}</b></div>{user && roleName(user.role) === 'tenant' ? <form onSubmit={onBook}><label>Move-in date<input name="moveInDate" type="date" min={today} defaultValue={property.availableFrom > today ? property.availableFrom : today} required /></label><label>Note to landlord<textarea name="message" placeholder="Introduce yourself and ask a question..." rows="3" /></label><button className="button button-dark full-button" type="submit">Request a viewing ↗</button></form> : <button className="button button-dark full-button" onClick={() => user ? onMessage() : onAuth()} type="button">{user ? 'Contact landlord ↗' : 'Sign in to enquire ↗'}</button>}<button className="button button-light full-button" onClick={onFavorite} type="button">{saved ? '♥ Saved to wishlist' : '♡ Save this home'}</button></div><div className="landlord-card"><p className="eyebrow">YOUR HOST</p><div className="landlord-person"><span className="avatar">{property.owner?.name?.slice(0, 1) || 'H'}</span><div><strong>{property.owner?.name || 'RentEase landlord'}</strong><small>Landlord on RentEase</small></div></div><div className="host-contact"><span>✉</span>{property.owner?.email || 'Contact through RentEase'}{property.owner?.phone ? <><br /><span>☎</span>{property.owner.phone}</> : null}</div></div></aside></div>
      </div>
    </div>
  );
}

function PropertyFormDialog({ form, setForm, files, setFiles, editing, busy, onClose, onSubmit, onDeleteImage }) {
  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const field = (name, label, type = 'text', required = true) => <label className="form-field">{label}<input type={type} value={form[name]} onChange={(event) => set(name, event.target.value)} required={required} /></label>;
  return <div className="modal-backdrop" onClick={onClose}><div className="modal-card form-modal" onClick={(event) => event.stopPropagation()}><ModalHeader eyebrow="LANDLORD STUDIO" title={editing ? 'Polish your listing' : 'List your home'} onClose={onClose} /><form onSubmit={onSubmit}><div className="form-grid">{field('title', 'Listing title')}{field('city', 'City')}{field('area', 'Neighbourhood')}{field('address', 'Street address')}{field('pincode', 'PIN code')}<label className="form-field">Home type<select value={form.propertyType} onChange={(event) => set('propertyType', event.target.value)}>{[['APARTMENT', 'Apartment'], ['INDEPENDENT_HOUSE', 'Independent house'], ['STUDIO', 'Studio'], ['VILLA', 'Villa'], ['ROOM', 'Room']].map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></label>{field('rent', 'Monthly rent (₹)', 'number')}{field('securityDeposit', 'Security deposit (₹)', 'number')}{field('bedrooms', 'Bedrooms', 'number')}{field('bathrooms', 'Bathrooms', 'number')}{field('sqft', 'Area (sq ft)', 'number', false)}<label className="form-field">Furnishing<select value={form.furnishedStatus} onChange={(event) => set('furnishedStatus', event.target.value)}><option value="FURNISHED">Furnished</option><option value="SEMI_FURNISHED">Semi-furnished</option><option value="UNFURNISHED">Unfurnished</option></select></label>{field('availableFrom', 'Available from', 'date', false)}{field('latitude', 'Latitude (optional)', 'number', false)}{field('longitude', 'Longitude (optional)', 'number', false)}<label className="form-field form-wide">Description<textarea value={form.description} onChange={(event) => set('description', event.target.value)} rows="4" required /></label><label className="form-field form-wide upload-field"><span>Property photos <small>Choose multiple images (up to 10 MB each)</small></span><input type="file" accept="image/*" multiple onChange={(event) => setFiles(Array.from(event.target.files || []))} />{files.length > 0 && <small>{files.map((file) => file.name).join(', ')}</small>}</label>{editing?.images?.length > 0 && <div className="form-field form-wide"><span>Current photos</span><div className="image-manager">{editing.images.map((image) => <div className="image-manager-item" key={image.id}><img src={imageSource(image.imageUrl)} alt={image.caption || 'Current listing photo'} /><button className="image-remove" type="button" aria-label={`Remove ${image.caption || 'photo'}`} onClick={() => onDeleteImage(editing, image)}>×</button></div>)}</div></div>}</div><div className="form-footer"><button className="button button-light" onClick={onClose} type="button">Cancel</button><button className="button button-dark" disabled={busy} type="submit">{busy ? 'Saving...' : editing ? 'Save changes' : 'Publish listing ↗'}</button></div></form></div></div>;
}

function AuthDialog({ mode, setMode, form, setForm, error, busy, onClose, onSubmit }) {
  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  return <div className="modal-backdrop auth-backdrop" onClick={onClose}><div className="modal-card auth-modal" onClick={(event) => event.stopPropagation()}><button className="icon-button auth-close" onClick={onClose} aria-label="Close sign in" type="button">×</button><div className="auth-visual"><span className="brand-mark">r</span><p className="eyebrow">A PLACE TO BEGIN</p><h2>Make room for<br /><em>something good.</em></h2><p>Find a thoughtful place to call your own.</p><div className="auth-visual-mark">⌂</div></div><div className="auth-form-side"><p className="eyebrow">{mode === 'login' ? 'WELCOME BACK' : 'JOIN RENTEASE'}</p><h2>{mode === 'login' ? 'Come on in.' : 'Find your place.'}</h2><p className="auth-intro">{mode === 'login' ? 'Sign in to pick up where you left off.' : 'A few details, then you’re on your way.'}</p><form onSubmit={onSubmit}>{mode === 'register' && <><label className="form-field">Full name<input value={form.name} onChange={(event) => set('name', event.target.value)} required minLength="2" /></label><label className="form-field">Phone number<input value={form.phone} onChange={(event) => set('phone', event.target.value)} required /></label><label className="form-field">I’m joining as<select value={form.role} onChange={(event) => set('role', event.target.value)}><option value="ROLE_TENANT">Looking for a home</option><option value="ROLE_LANDLORD">Listing a home</option></select></label></>}<label className="form-field">Email address<input type="email" value={form.email} onChange={(event) => set('email', event.target.value)} required autoComplete="email" /></label><label className="form-field">Password<input type="password" value={form.password} onChange={(event) => set('password', event.target.value)} required minLength="6" autoComplete={mode === 'login' ? 'current-password' : 'new-password'} /></label>{error && <p className="form-error" role="alert">{error}</p>}<button className="button button-dark full-button" disabled={busy} type="submit">{busy ? 'One moment...' : mode === 'login' ? 'Sign in to RentEase ↗' : 'Create your account ↗'}</button></form><p className="auth-switch">{mode === 'login' ? 'New around here?' : 'Already have an account?'} <button onClick={() => setMode(mode === 'login' ? 'register' : 'login')} type="button">{mode === 'login' ? 'Create an account' : 'Sign in'}</button></p></div></div></div>;
}

export default App;
